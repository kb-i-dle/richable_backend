package com.idle.kb_i_dle_backend.domain.goal.service.Impl;

import com.idle.kb_i_dle_backend.config.exception.CustomException;
import com.idle.kb_i_dle_backend.domain.finance.dto.FinancialSumDTO;
import com.idle.kb_i_dle_backend.domain.finance.service.FinanceService;
import com.idle.kb_i_dle_backend.domain.goal.dto.*;
import com.idle.kb_i_dle_backend.domain.goal.entity.Goal;
import com.idle.kb_i_dle_backend.domain.goal.repository.GoalRepository;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {
    @Mock
    private GoalRepository goalRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private FinanceService financeService;

    @InjectMocks
    private GoalServiceImpl goalServiceImpl;

    @Transactional
    @Test
    void saveGoal() {
        // 필요한 객체 설정
        Integer uid = 1;
        Member member = new Member();

        // AddGoalDTO를 Mock 객체로 생성하여 getCategory() 호출 시 "소비"를 반환하도록 설정
        AddGoalDTO addGoalDTO = mock(AddGoalDTO.class);
        when(addGoalDTO.getCategory()).thenReturn("자산");

        // Mock 설정: memberService와 goalRepository 동작 설정
        when(memberService.findMemberByUid(uid)).thenReturn(member);

        // Goal 객체를 Mocking하여 반환
        Goal mockGoal = mock(Goal.class);
        when(goalRepository.save(any(Goal.class))).thenReturn(mockGoal);
        when(mockGoal.getCategory()).thenReturn("자산");
        when(mockGoal.getTitle()).thenReturn("Test Goal");

        GoalDTO result = goalServiceImpl.saveGoal(uid, addGoalDTO);

        // 결과 검증
        assertThat(result.getCategory()).isEqualTo("자산");
        assertThat(result.getTitle()).isEqualTo("Test Goal");
    }

    @Transactional
    @Test
    void saveOutcomeGoal() {
        // 필요한 객체 설정
        Integer uid = 1;
        Member member = new Member();

        // AddGoalDTO를 Mock 객체로 생성하여 getCategory() 호출 시 "소비"를 반환하도록 설정
        AddGoalDTO addGoalDTO = mock(AddGoalDTO.class);
        when(addGoalDTO.getCategory()).thenReturn("소비");
        when(addGoalDTO.getTitle()).thenReturn("Outcome Goal");
        when(addGoalDTO.getAmount()).thenReturn(500L);

        // Mock 설정: memberService와 goalRepository 동작 설정
        when(memberService.findMemberByUid(uid)).thenReturn(member);

        // Goal 객체를 Mocking하여 반환
        Goal mockGoal = mock(Goal.class);
        when(goalRepository.save(any(Goal.class))).thenReturn(mockGoal);
        when(mockGoal.getCategory()).thenReturn("소비");
        when(mockGoal.getTitle()).thenReturn("Outcome Goal");
        when(mockGoal.getAmount()).thenReturn(500L);

        GoalDTO result = goalServiceImpl.saveGoal(uid, addGoalDTO);

        // 결과 검증
        assertThat(result.getCategory()).isEqualTo("소비");
        assertThat(result.getTitle()).isEqualTo("Outcome Goal");
        assertThat(result.getAmount()).isEqualTo(500L);
    }

    @Transactional
    @Test
    void saveAssetGoal() {
        // 필요한 객체 설정
        Integer uid = 1;
        Member member = new Member();

        // AddGoalDTO를 Mock 객체로 생성하여 getCategory() 호출 시 "자산"을 반환하도록 설정
        AddGoalDTO addGoalDTO = mock(AddGoalDTO.class);
        when(addGoalDTO.getCategory()).thenReturn("자산");
        when(addGoalDTO.getTitle()).thenReturn("Asset Goal");
        when(addGoalDTO.getAmount()).thenReturn(1000L);

        // Mock 설정: memberService와 goalRepository 동작 설정
        when(memberService.findMemberByUid(uid)).thenReturn(member);

        // Goal 객체를 Mocking하여 반환
        Goal mockGoal = mock(Goal.class);
        when(goalRepository.save(any(Goal.class))).thenReturn(mockGoal);
        when(mockGoal.getCategory()).thenReturn("자산");
        when(mockGoal.getTitle()).thenReturn("Asset Goal");
        when(mockGoal.getAmount()).thenReturn(1000L);

        GoalDTO result = goalServiceImpl.saveGoal(uid, addGoalDTO);

        // 결과 검증
        assertThat(result.getCategory()).isEqualTo("자산");
        assertThat(result.getTitle()).isEqualTo("Asset Goal");
        assertThat(result.getAmount()).isEqualTo(1000L);
    }

    @Transactional
    @Test
    void updateAchive() {
        int uid = 1;

        // RequestIndexDTO 인스턴스 생성 후 인덱스 설정
        RequestIndexDTO requestIndexDTO = new RequestIndexDTO();
        requestIndexDTO.setIndex(999);

        // Mock 설정: Member와 존재하는 Goal로 설정, 그러나 getById는 예외를 발생하도록 설정
        Member member = new Member();
        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(goalRepository.existsByUidAndIndex(member, requestIndexDTO.getIndex())).thenReturn(true);
        when(goalRepository.getById(requestIndexDTO.getIndex())).thenThrow(new RuntimeException("데이터베이스 오류"));

        // CustomException 발생 확인
        assertThrows(CustomException.class, () -> goalServiceImpl.updateAchive(uid, requestIndexDTO));
    }

    @Transactional
    @Test
    void updatePriority() {
        int uid = 1;
        int newPriority = 1;
        int goalIndex = 999;
        Member member = new Member();

        // RequestPriorityDTO 생성하여 설정
        RequestPriorityDTO requestPriorityDTO = mock(RequestPriorityDTO.class);
        when(requestPriorityDTO.getIndex()).thenReturn(goalIndex);
        when(requestPriorityDTO.getPriority()).thenReturn(newPriority);

        // Mock 설정
        Goal goal = mock(Goal.class);
        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(goalRepository.existsByUidAndIndex(member, goalIndex)).thenReturn(true);
        when(goalRepository.getById(goalIndex)).thenReturn(goal);

        // 우선순위 업데이트를 위한 목표 수 설정
        when(goalRepository.countByUidAndCategoryAndIsAchive(member, "소비", false)).thenReturn(2); // 허용 범위 내의 값

        // 기존 우선순위 설정 및 새로운 우선순위 설정
        when(goal.getPriority()).thenReturn(newPriority); // 업데이트된 우선순위를 반환하도록 설정

        // 우선순위 업데이트 호출
        ResponseUpdateAchiveDTO result = goalServiceImpl.updatePriority(uid, requestPriorityDTO);

        // 결과 검증
        verify(goal).updatePriority(newPriority);  // updatePriority 메서드 호출 검증
        assertThat(result.getPriority()).isEqualTo(newPriority);
    }

    @Test
    void getOutcomeGoals() {
        int uid = 1;
        Member member = new Member();

        // Mocked Goal 객체 생성
        Goal goal1 = mock(Goal.class);
        Goal goal2 = mock(Goal.class);
        when(goal1.getDate()).thenReturn(new Date());
        when(goal2.getDate()).thenReturn(new Date());

        // goals 리스트에 Mocked 객체 포함
        List<Goal> goals = Arrays.asList(goal1, goal2);

        // Mock 설정
        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(goalRepository.findByUidAndCategoryAndIsAchive(member, "소비", false)).thenReturn(goals);
        when(financeService.getAssetSummeryByDateBefore(eq(uid), any(Date.class)))
                .thenReturn(new FinancialSumDTO(2000L));

        List<OutcomeGoalDTO> result = goalServiceImpl.getOutcomeGoals(uid);

        // 결과 검증
        assertThat(result.size()).isEqualTo(goals.size());
        assertThat(result.get(0).getGather()).isGreaterThanOrEqualTo(0L);
    }

    @Test
    void getAssetGoal() {
        int uid = 1;
        Member member = new Member();
        Goal assetGoal = mock(Goal.class);

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(goalRepository.existsByUidAndCategoryAndIsAchive(member, "자산", false)).thenReturn(true);
        when(goalRepository.findFirstByUidAndCategoryAndIsAchive(member, "자산", false)).thenReturn(assetGoal);

        // Mock 설정: 날짜 반환을 설정
        Date date = new Date();
        when(assetGoal.getDate()).thenReturn(date);
        when(financeService.getAssetSummeryByDateBefore(eq(uid), any(Date.class)))
                .thenReturn(new FinancialSumDTO(3000L));

        AssetGoalDTO result = goalServiceImpl.getAssetGoal(uid);

        // 결과 검증
        assertThat(result.getAmount()).isGreaterThanOrEqualTo(0L);
    }
}