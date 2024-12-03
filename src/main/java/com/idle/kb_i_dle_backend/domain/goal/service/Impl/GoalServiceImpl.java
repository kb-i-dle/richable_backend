package com.idle.kb_i_dle_backend.domain.goal.service.Impl;

import com.idle.kb_i_dle_backend.config.exception.CustomException;
import com.idle.kb_i_dle_backend.domain.finance.dto.FinancialSumDTO;
import com.idle.kb_i_dle_backend.domain.finance.service.FinanceService;
import com.idle.kb_i_dle_backend.domain.goal.dto.AddGoalDTO;
import com.idle.kb_i_dle_backend.domain.goal.dto.AssetGoalDTO;
import com.idle.kb_i_dle_backend.domain.goal.dto.GoalDTO;
import com.idle.kb_i_dle_backend.domain.goal.dto.OutcomeGoalDTO;
import com.idle.kb_i_dle_backend.domain.goal.dto.RequestDeleteDTO;
import com.idle.kb_i_dle_backend.domain.goal.dto.RequestIndexDTO;
import com.idle.kb_i_dle_backend.domain.goal.dto.RequestPriorityDTO;
import com.idle.kb_i_dle_backend.domain.goal.dto.ResponseIndexDTO;
import com.idle.kb_i_dle_backend.domain.goal.dto.ResponseUpdateAchiveDTO;
import com.idle.kb_i_dle_backend.domain.goal.entity.Goal;
import com.idle.kb_i_dle_backend.domain.goal.repository.GoalRepository;
import com.idle.kb_i_dle_backend.domain.goal.service.GoalService;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.global.codes.ErrorCode;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final MemberService memberService;
    private final FinanceService financeService;

    /**
     * 목표 저장
     *
     * @param uid
     * @param addGoalDTO
     * @return
     */
    @Override
    public GoalDTO saveGoal(int uid, AddGoalDTO addGoalDTO) {

        try {//맞는 유저인지
            Member member = memberService.findMemberByUid(uid);

            //목표 카테고리 맞는지
            if (addGoalDTO.getCategory().equals("소비")) {
                return saveOutcomeGoal(member, addGoalDTO);
            } else if (addGoalDTO.getCategory().equals("자산")) {
                return saveAssetGoal(member, addGoalDTO);
            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_CATEGORY, "없는 카테고리 입니다.");
        }
        return null;
    }

    @Override
    public GoalDTO saveOutcomeGoal(Member member, AddGoalDTO addGoalDTO) {
        try {
            // 사용자가 현재 가지고 있는 목표의 개수를 가져옴
            int currentGoalCount = goalRepository.countByUidAndCategoryAndIsAchive(member, addGoalDTO.getCategory(), false);
            //목표 생성
            Goal goal = new Goal(member, addGoalDTO.getCategory(), addGoalDTO.getTitle(), addGoalDTO.getAmount(),
                    currentGoalCount + 1);
            //목표 저장
            Goal responseGoal = goalRepository.save(goal);
            return new GoalDTO(responseGoal.getCategory(), responseGoal.getTitle(), responseGoal.getAmount(),
                    responseGoal.getPriority());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.GOAL_CREATE_FAILED, "소비 목표 생성 중 오류 발생");
        }
    }

    @Override
    public GoalDTO saveAssetGoal(Member member, AddGoalDTO addGoalDTO) {
        try {
            // 사용자가 현재 가지고 있는 목표의 개수를 가져옴
            int currentGoalCount = goalRepository.countByUidAndCategoryAndIsAchive(member, addGoalDTO.getCategory(), false);
            //목표 생성
            if (currentGoalCount == 0) {
                Goal goal = new Goal(member, addGoalDTO.getCategory(), addGoalDTO.getTitle(), addGoalDTO.getAmount(),
                        currentGoalCount + 1);
                //목표 저장
                Goal responseGoal = goalRepository.save(goal);
                return new GoalDTO(responseGoal.getCategory(), responseGoal.getTitle(), responseGoal.getAmount(),
                        responseGoal.getPriority());
            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_GOAL, "이미 자산 목표가 있습니다.");
        }
        return null;
    }

    @Override
    @Transactional
    public ResponseUpdateAchiveDTO updateAchive(int uid, RequestIndexDTO requestIndexDTO) {
        try {
            Member member = memberService.findMemberByUid(uid);

            if (goalRepository.existsByUidAndIndex(member, requestIndexDTO.getIndex())) {
                //해당인덱스의 goal을 가져오고
                Goal goal = goalRepository.getById(requestIndexDTO.getIndex());
                //achive와 priority를 수정
                goal.updateToAchive();
                //responseUpdateachive에 반환
                return new ResponseUpdateAchiveDTO(goal.getIndex(), goal.getIsAchive(), goal.getPriority());
            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_INDEX, "달성할 수 없습니다.");
        }
        return null;
    }


    @Override
    @Transactional
    public ResponseIndexDTO removeGoal(int uid, RequestDeleteDTO requestDeleteDTO) {
        Member member = memberService.findMemberByUid(uid);
        try {
            // 해당 goal이 있는지 확인
            if (goalRepository.existsByUidAndIndex(member, requestDeleteDTO.getIndex())) {
                // 해당 목표 삭제

                Goal deleteGoal = goalRepository.findByUidAndIndex(member, requestDeleteDTO.getIndex());
                goalRepository.deleteByUidAndIndex(member, requestDeleteDTO.getIndex());
                if (requestDeleteDTO.getCategory().equals("소비")) {

                    //미리 삭제하는 우선 순위 저장
                    int oldPriority = deleteGoal.getPriority();
                    List<Goal> currentGoals = goalRepository.findByUidAndCategoryAndIsAchive(member, "소비", false);
                    for (Goal goal : currentGoals) {
                        if (goal.getPriority() > oldPriority) {
                            goal.updatePriority(goal.getPriority() - 1);
                        }
                    }

                    goalRepository.saveAll(currentGoals);

                    return new ResponseIndexDTO(deleteGoal.getIndex());
                }
                if (deleteGoal.getCategory().equals("자산")) {
                    return new ResponseIndexDTO(deleteGoal.getIndex());
                }

                return new ResponseIndexDTO(requestDeleteDTO.getIndex());
            }

        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_INDEX, "제거할 수 없는 목표입니다.");
        }
        return null;
    }


    @Override
    @Transactional
    public ResponseUpdateAchiveDTO updatePriority(int uid, RequestPriorityDTO requestPriorityDTO) {
        try {
            Member member = memberService.findMemberByUid(uid);

            if (goalRepository.existsByUidAndIndex(member, requestPriorityDTO.getIndex())) {

                int currentGoalCount = goalRepository.countByUidAndCategoryAndIsAchive(member, "소비", false);
                List<Goal> currentGoals = goalRepository.findByUidAndCategoryAndIsAchive(member, "소비", false);
                if (requestPriorityDTO.getPriority() < 0 || requestPriorityDTO.getPriority() > currentGoalCount) {
                    throw new CustomException(ErrorCode.INVALID_PRIORITY, "우선 순위 값을 제대로 설정해주세요");
                }

                Goal targetGoal = goalRepository.getById(requestPriorityDTO.getIndex());
                int oldPriority = targetGoal.getPriority();

                targetGoal.updatePriority(requestPriorityDTO.getPriority());

                for (Goal goal : currentGoals) {
                    if (goal != targetGoal) {
                        if (goal.getPriority() < oldPriority && goal.getPriority() >= requestPriorityDTO.getPriority()) {
                            goal.updatePriority(goal.getPriority() + 1);
                        } else if (goal.getPriority() > oldPriority
                                && goal.getPriority() <= requestPriorityDTO.getPriority()) {
                            goal.updatePriority(goal.getPriority() - 1);
                        }
                    }
                }

                goalRepository.saveAll(currentGoals);
                return new ResponseUpdateAchiveDTO(requestPriorityDTO.getIndex(), targetGoal.getIsAchive(),
                        targetGoal.getPriority());
            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_INDEX, "우선순위 변경 중 오류 발생");
        }
        return null;
    }


    @Override
    public List<OutcomeGoalDTO> getOutcomeGoals(int uid) {
        try {
            Member member = memberService.findMemberByUid(uid);
            //소비 목표들을 불러옴
            List<Goal> outcomeGoals = goalRepository.findByUidAndCategoryAndIsAchive(member, "소비", false);

            //가장 오래된 날짜를 가져옴
            Date oldDate = outcomeGoals.stream().map(Goal::getDate).min(Date::compareTo).orElseThrow();
            //현재 자산에서 해당 날짜의 자산 을 빼서 모은 금액을 구함
            FinancialSumDTO oldDateAssetSum = financeService.getAssetSummeryByDateBefore(uid, oldDate);
            log.info("new Date!!!!!!!!!!!!!" + new Date());
            FinancialSumDTO todayAssetSum = financeService.getAssetSummeryByDateBefore(uid,
                    new Timestamp(System.currentTimeMillis()));
            long amount = todayAssetSum.getAmount() - oldDateAssetSum.getAmount();
            //모은 금액을 우선 순위에 맞게 분배
            //먼저 정렬
            outcomeGoals.sort(Comparator.comparing(Goal::getPriority));
            //gather을 계산하면서 DTO로 만듦
            List<OutcomeGoalDTO> outcomeGoalDTOS = new ArrayList<>();
            for (Goal goal : outcomeGoals) {
                if (amount > goal.getAmount()) {
                    amount -= goal.getAmount();
                    outcomeGoalDTOS.add(
                            new OutcomeGoalDTO(goal.getIndex(), goal.getAmount(), goal.getTitle(), goal.getAmount(),
                                    goal.getDate(), goal.getPriority()));
                } else {
                    outcomeGoalDTOS.add(
                            new OutcomeGoalDTO(goal.getIndex(), amount, goal.getTitle(), goal.getAmount(), goal.getDate(),
                                    goal.getPriority()));
                    amount = 0L;
                }
            }
            return outcomeGoalDTOS;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.GOAL_NOT_FOUND, "소비 목표 조회 중 오류 발생");
        }
    }

//
//    private OutcomeGoalDTO calculateGather(Long amount, Goal goal){
//        if(amount > goal.getAmount()){
//            amount -= goal.getAmount();
//            return new OutcomeGoalDTO(goal.getIndex(),
//                    goal.getAmount(), goal.getTitle(),goal.getAmount(),goal.getDate(),goal.getPriority());
//        }else{
//            return new OutcomeGoalDTO(goal.getIndex(),
//                    amount, goal.getTitle(),goal.getAmount(),goal.getDate(),goal.getPriority());
//        }
//    }


    @Override
    public AssetGoalDTO getAssetGoal(int uid) {
        try {
            Member member = memberService.findMemberByUid(uid);
            //유저의 자산 목표를 가져옴
            if (goalRepository.existsByUidAndCategoryAndIsAchive(member, "자산", false)) {
                Goal goal = goalRepository.findFirstByUidAndCategoryAndIsAchive(member, "자산", false);

                //날짜 정보를 가져옴
                Date madeDate = goal.getDate();

                FinancialSumDTO oldDateAssetSum = financeService.getAssetSummeryByDateBefore(uid, madeDate);
                log.info("new Date!!!!!!!!!!!!!" + new Date());
                FinancialSumDTO todayAssetSum = financeService.getAssetSummeryByDateBefore(uid,
                        new Timestamp(System.currentTimeMillis()));

                //모은 금액
                long amount = todayAssetSum.getAmount() - oldDateAssetSum.getAmount();

                if (amount >= goal.getAmount()) {
                    return new AssetGoalDTO(goal.getIndex(), amount, goal.getTitle(), goal.getAmount(), goal.getDate(), 0);
                } else { //덜 모았을때
                    // 덜 모았을 때 남은 날짜 계산
                    long remainingAmount = goal.getAmount() - amount;
                    long daysPassed = (new Date().getTime() - madeDate.getTime()) / (1000 * 60 * 60 * 24);

                    int remainDate;
                    if (daysPassed > 0 && amount > 0) {
                        long dailyRate = amount / daysPassed;

                        if (dailyRate > 0) {
                            remainDate = (int) (remainingAmount / dailyRate);
                        } else {
                            remainDate = 0;
                        }
                    } else {
                        remainDate = 0;  // 날짜가 지나지 않았거나 금액이 없으면 -1로 설정
                    }
                    return new AssetGoalDTO(goal.getIndex(), amount, goal.getTitle(), goal.getAmount(), goal.getDate(),
                            remainDate);
                }
            } else {
                return new AssetGoalDTO();
            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.GOAL_NOT_FOUND, "자산 목표 조회 중 오류 발생");
        }
    }
}
