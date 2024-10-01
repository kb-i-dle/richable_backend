package com.idle.kb_i_dle_backend.domain.outcome.service;

import com.idle.kb_i_dle_backend.domain.member.entity.User;
import com.idle.kb_i_dle_backend.domain.member.repository.UserRepository;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.domain.outcome.dto.*;
import com.idle.kb_i_dle_backend.domain.outcome.entity.OutcomeAverage;
import com.idle.kb_i_dle_backend.domain.outcome.entity.OutcomeCategory;
import com.idle.kb_i_dle_backend.domain.outcome.entity.OutcomeUser;
import com.idle.kb_i_dle_backend.domain.outcome.repository.AverageOutcomeRepository;
import com.idle.kb_i_dle_backend.domain.outcome.repository.CategoryRepository;
import com.idle.kb_i_dle_backend.domain.outcome.repository.OutcomeUserRepository;

import java.time.LocalDate;
import java.time.Period;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutcomeServiceImpl implements OutcomeService {

    private final AverageOutcomeRepository averageOutcomeRepository;
    private final OutcomeUserRepository outcomeUserRepository;
    private final CategoryRepository categoryRepository;
    private final MemberService memberService;
    private final UserRepository userRepository;


    /**
     * 카테고리마다 소비 합 과 전체 소비 합 조회
     * @param year
     * @param month
     * @return
     */
    @Override
    public ResponseCategorySumListDTO findCategorySum(Integer year, Integer month) {
        List<CategorySumDTO> categorySumDTOS = outcomeUserRepository.findCategorySumByUidAndYearAndMonth(1 , year, month);
        Long sum = categorySumDTOS.stream().mapToLong(CategorySumDTO::getSum).sum();
        return new ResponseCategorySumListDTO(categorySumDTOS, sum);
    }


    /**
     * 해당 달의 소비 누계
     * @param year
     * @param month
     * @return
     */
    @Override
    public MonthOutcomeDTO findMonthOutcome(Integer year, Integer month) {
        List<OutcomeUser> consumes = outcomeUserRepository.findAmountAllByUidAndYearAndMonth(1, year, month);
        List<Long> dailyAmount = new ArrayList<>(Collections.nCopies(31, 0L));
        for(OutcomeUser consume : consumes) {
            Date date = consume.getDate();
            int day = date.getDate();
            System.out.println(date.toString() + " " + consume.getAmount());
            dailyAmount.set(day -1 , dailyAmount.get(day -1 ) + consume.getAmount()) ;
        }

        // 누적합 계산
        long cumulativeSum = 0L;
        for (int i = 0; i < dailyAmount.size(); i++) {
            cumulativeSum += dailyAmount.get(i);  // 이전까지의 합을 더함
            dailyAmount.set(i, cumulativeSum);    // 누적합을 다시 리스트에 저장
        }

        System.out.println(consumes.toString());
        MonthOutcomeDTO monthOutcomeDTO = new MonthOutcomeDTO(month,year,dailyAmount);

        return monthOutcomeDTO;
    }

    /**
     * start에서 end까지 해당 카테고리의 소비량을 평균과 비교
     * @param uid
     * @param start
     * @param end
     * @param category
     * @return
     */
    @Override
    @Transactional
    public CompareAverageCategoryOutcomeDTO compareWithAverage(int uid, Date start, Date end, String category) {
        //먼저 사용자 uid를 가져오고
        Optional<User> optionalUser = memberService.findMemberByUid(uid);
        //dto 생성
        CompareAverageCategoryOutcomeDTO compareAverageCategoryOutcomeDTO = new CompareAverageCategoryOutcomeDTO();

        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            //사용자의 나이대를 가져온다.
            String ageRange =  getUserAgeRange(getUserAge(user.getBirth_year()));
            return getCompareAverageCategoryOutcomeDTO(user, start, end, category, ageRange, compareAverageCategoryOutcomeDTO);
        }
        return compareAverageCategoryOutcomeDTO;

    }


    /**
     * start에서 end까지 아낄 수 잇었던 비용
     * @param uid
     * @param start
     * @param end
     * @return
     */
    @Override
    public PossibleSaveOutcomeInMonthDTO findPossibleSaveOutcome(Integer uid, Date start, Date end) {
        //먼저 사용자 uid를 가져오고
        Optional<User> optionalUser = memberService.findMemberByUid(uid);
        //dto 생성
        PossibleSaveOutcomeInMonthDTO possibleSaveOutcomeInMonthDTO = new PossibleSaveOutcomeInMonthDTO();

        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            //사용자의 나이대를 가져온다.
            String ageRange =  getUserAgeRange(getUserAge(user.getBirth_year()));

            //전체 소비 조회
            List<OutcomeUser> outcomeUsers = outcomeUserRepository.findByUidAndDateBetween(user, start, end);

            //평균 전체 소비
            List<OutcomeAverage> outcomeAverages = averageOutcomeRepository.findByAgeGroupAndQuater(ageRange, getQuarter(start));

            Long userSum = outcomeUsers.stream().mapToLong(OutcomeUser::getAmount).sum();
            Long averageSum = outcomeAverages.stream().mapToLong(OutcomeAverage::getOutcome).sum();
            log.info("userSum = " + userSum);
            log.info("averageSum = " + averageSum);
            possibleSaveOutcomeInMonthDTO.setPossibleSaveAmount(averageSum - userSum);
            return possibleSaveOutcomeInMonthDTO;
        }
        return possibleSaveOutcomeInMonthDTO;
    }

    /**
     * 해당 카테고리, 나이대, start~end까지의 user의 평균 소비량 비교DTO를 가져온다.
     * @param user
     * @param start
     * @param end
     * @param category
     * @param ageRange
     * @param compareAverageCategoryOutcomeDTO
     * @return
     */
    public CompareAverageCategoryOutcomeDTO getCompareAverageCategoryOutcomeDTO(User user, Date start, Date end, String category, String ageRange, CompareAverageCategoryOutcomeDTO compareAverageCategoryOutcomeDTO){
        //사용자의 한달 동안 소비 조회
        List<OutcomeUser> outcomeUsers = outcomeUserRepository.findByUidAndDateBetween(user, start, end);

        //카테고리 조회
        OutcomeCategory outcomeCategory = categoryRepository.findByCategoryNameStartingWith(category);

        //date를 분기로 변경
        //사용자 나이대의 카테고리의 소비를 조회
        List<OutcomeAverage> outcomeAverages = averageOutcomeRepository.findByCategoryAndQuaterAndAgeGroup(outcomeCategory.getCategoryName(), getQuarter(start), ageRange);
        String categoryName = outcomeCategory.getCategoryName();
        compareAverageCategoryOutcomeDTO.setMySum( outcomeUsers.stream().mapToLong(OutcomeUser::getAmount).sum());
        compareAverageCategoryOutcomeDTO.setAverageSum(outcomeAverages.stream().mapToLong(OutcomeAverage::getOutcome).sum());
        compareAverageCategoryOutcomeDTO.setCategory(outcomeCategory.getCategoryName());
        return compareAverageCategoryOutcomeDTO;
    }

    /**
     * 사용자 나이대를 가져옴
     * @param age
     * @return
     */
    private String getUserAgeRange(int age){
        if(age <  40){ //30대 이하
            return "30대 이하";
        } else if(age < 50) { //40대
            return "40대";
        } else if( age < 60) { //50 대
            return "50대";
        } else if( age < 65) { //60세 이상
            return "60세이상 가구";
        } // 65세 이상
        return "65세이상 가구";
    }

    /**
     * 사용자의 나이를 가져옴
     * @param birthYear
     * @return
     */
    private int getUserAge(int birthYear){
        LocalDate birthDate = LocalDate.of(birthYear, 1, 1);
        LocalDate currentDate = LocalDate.now();

        return Period.between(birthDate , currentDate).getYears();
    }

    /**
     * 몇 분기인지 반환
     * @param date
     * @return
     */
    private String getQuarter(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // 0-based, 0=January


        StringBuffer sb = new StringBuffer();
        return sb.append(year).append("년 ").append(month/ 4 + 1).append("분기").toString();

    }
}
