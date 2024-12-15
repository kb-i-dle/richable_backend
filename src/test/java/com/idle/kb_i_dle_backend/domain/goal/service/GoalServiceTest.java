package com.idle.kb_i_dle_backend.domain.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.idle.kb_i_dle_backend.domain.goal.exception.GoalException;
import com.idle.kb_i_dle_backend.domain.goal.repository.GoalRepository;
import com.idle.kb_i_dle_backend.domain.goal.service.Impl.GoalServiceImpl;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.global.codes.ErrorCode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private FinanceService financeService;

    @InjectMocks
    private GoalServiceImpl goalService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveGoal_withOutcomeCategory() {
        // given
        int uid = 1;
        AddGoalDTO addGoalDTO = new AddGoalDTO("소비", "Test Goal", 1000);
        Member member = new Member();
        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(goalRepository.countByUidAndCategoryAndIsAchive(member, "소비", false)).thenReturn(0);
        when(goalRepository.save(any(Goal.class))).thenReturn(new Goal(member, "소비", "Test Goal", 1000L, 1));

        // when
        GoalDTO result = goalService.saveGoal(uid, addGoalDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCategory()).isEqualTo("소비");
        assertThat(result.getTitle()).isEqualTo("Test Goal");
        assertThat(result.getAmount()).isEqualTo(1000L);
    }

    @Test
    public void testSaveGoal_withAssetCategory() {
        // given
        int uid = 1;
        AddGoalDTO addGoalDTO = new AddGoalDTO("자산", "Test Goal", 1000);
        Member member = new Member();
        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(goalRepository.countByUidAndCategoryAndIsAchive(member, "자산", false)).thenReturn(0);
        when(goalRepository.save(any(Goal.class))).thenReturn(new Goal(member, "자산", "Test Goal", 1000L, 1));

        // when
        GoalDTO result = goalService.saveGoal(uid, addGoalDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCategory()).isEqualTo("자산");
        assertThat(result.getTitle()).isEqualTo("Test Goal");
        assertThat(result.getAmount()).isEqualTo(1000L);
    }

    @Test
    public void testSaveGoal_withInvalidCategory() {
        // given
        int uid = 1;
        AddGoalDTO addGoalDTO = new AddGoalDTO("잘못된 카테고리", "Test Goal", 1000);
        Member member = new Member();
        when(memberService.findMemberByUid(uid)).thenReturn(member);

        // when & then
        assertThatThrownBy(() -> goalService.saveGoal(uid, addGoalDTO))
                .isInstanceOf(GoalException.class)
                .hasMessage("없는 카테고리 입니다.")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_CATEGORY);
    }

    @Test
    public void testSaveOutcomeGoal() {
        // given
        Member member = new Member();
        String category = "소비";
        AddGoalDTO addGoalDTO = new AddGoalDTO(category, "Test Goal", 1000);
        int currentGoalCount = 2;
        when(goalRepository.countByUidAndCategoryAndIsAchive(member, category, false)).thenReturn(currentGoalCount);

        Goal savedGoal = new Goal(member, category, "Test Goal", 1000L, currentGoalCount + 1);
        when(goalRepository.save(any(Goal.class))).thenReturn(savedGoal);

        // when
        GoalDTO result = goalService.saveOutcomeGoal(member, addGoalDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCategory()).isEqualTo("소비");
        assertThat(result.getTitle()).isEqualTo("Test Goal");
        assertThat(result.getAmount()).isEqualTo(1000L);
        assertThat(result.getPriority()).isEqualTo(currentGoalCount + 1);
    }

    @Test
    public void testSaveAssetGoal_whenNoExistingGoals() {
        // given
        Member member = new Member();
        AddGoalDTO addGoalDTO = new AddGoalDTO("자산", "Test Asset Goal", 5000);
        int currentGoalCount = 0;
        when(goalRepository.countByUidAndCategoryAndIsAchive(member, "자산", false)).thenReturn(currentGoalCount);

        Goal savedGoal = new Goal(member, "자산", "Test Asset Goal", 5000L, currentGoalCount + 1);
        when(goalRepository.save(any(Goal.class))).thenReturn(savedGoal);

        // when
        GoalDTO result = goalService.saveAssetGoal(member, addGoalDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCategory()).isEqualTo("자산");
        assertThat(result.getTitle()).isEqualTo("Test Asset Goal");
        assertThat(result.getAmount()).isEqualTo(5000L);
        assertThat(result.getPriority()).isEqualTo(currentGoalCount + 1);
    }

    @Test
    public void testSaveAssetGoal_whenExistingGoalsPresent() {
        // given
        Member member = new Member();
        AddGoalDTO addGoalDTO = new AddGoalDTO("자산", "Test Asset Goal", 5000);
        int currentGoalCount = 1;
        when(goalRepository.countByUidAndCategoryAndIsAchive(member, "자산", false)).thenReturn(currentGoalCount);

        // when & then
        assertThatThrownBy(() -> goalService.saveAssetGoal(member, addGoalDTO))
                .isInstanceOf(GoalException.class)
                .hasMessage("이미 자산 목표가 있습니다.")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_GOAL);
    }

    @Test
    public void testUpdateAchive_withValidIndex() {
        // given
        int uid = 1;
        int index = 100;
        RequestIndexDTO requestIndexDTO = new RequestIndexDTO(index);
        Member member = new Member();
        Goal goal = new Goal(member, "소비", "test", 10000L, index);

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(goalRepository.existsByUidAndIndex(member, index)).thenReturn(true);
        when(goalRepository.getById(index)).thenReturn(goal);

        // when
        ResponseUpdateAchiveDTO result = goalService.updateAchive(uid, requestIndexDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getIndex()).isEqualTo(index);
        assertThat(result.getIsAchive()).isTrue();
        assertThat(result.getPriority()).isEqualTo(goal.getPriority());
    }

    @Test
    public void testUpdateAchive_withInvalidIndex() {
        // given
        int uid = 1;
        int index = 100;
        RequestIndexDTO requestIndexDTO = new RequestIndexDTO(index);
        Member member = new Member();

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(goalRepository.existsByUidAndIndex(member, index)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> goalService.updateAchive(uid, requestIndexDTO))
                .isInstanceOf(GoalException.class)
                .hasMessage("없는 index입니다.")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_INDEX);
    }

    /**
     * 소비 목표 삭제
     */
    public void testRemoveGoal_withValidIndexAndCategoryConsumption() {
        // given
        int uid = 1;
        int index = 100;
        String category = "소비";
        RequestDeleteDTO requestDeleteDTO = new RequestDeleteDTO(index, "소비");
        Member member = new Member();
        Goal deleteGoal = new Goal(member, category, "test", 1000L, index);

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(goalRepository.existsByUidAndIndex(member, index)).thenReturn(true);
        when(goalRepository.findByUidAndIndex(member, index)).thenReturn(deleteGoal);

        List<Goal> currentGoals = new ArrayList<>();
        Goal goal1 = new Goal(index, member, category);
        currentGoals.add(goal1);

        when(goalRepository.findByUidAndCategoryAndIsAchive(member, "소비", false)).thenReturn(currentGoals);

        // when
        ResponseIndexDTO result = goalService.removeGoal(uid, requestDeleteDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getIndex()).isEqualTo(index);
        verify(goalRepository).deleteByUidAndIndex(member, index);
        verify(goalRepository).saveAll(currentGoals);
        assertThat(currentGoals.get(0).getPriority()).isEqualTo(1);
    }

    @Test
    public void testRemoveGoal_withValidIndexAndCategoryAsset() {
        // given
        int uid = 1;
        int index = 100;
        String category = "자산";
        RequestDeleteDTO requestDeleteDTO = new RequestDeleteDTO(index, category);
        Member member = new Member();
        Goal deleteGoal = new Goal(index, member, category);

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(goalRepository.existsByUidAndIndex(member, index)).thenReturn(true);
        when(goalRepository.findByUidAndIndex(member, index)).thenReturn(deleteGoal);

        // when
        ResponseIndexDTO result = goalService.removeGoal(uid, requestDeleteDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getIndex()).isEqualTo(index);
        verify(goalRepository).deleteByUidAndIndex(member, index);
    }

    @Test
    public void testRemoveGoal_withInvalidIndex() {
        // given
        int uid = 1;
        int index = 100;
        RequestDeleteDTO requestDeleteDTO = new RequestDeleteDTO(index, "소비");
        Member member = new Member();

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(goalRepository.existsByUidAndIndex(member, index)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> goalService.removeGoal(uid, requestDeleteDTO))
                .isInstanceOf(GoalException.class)
                .hasMessage("없는 index입니다.")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_INDEX);
    }

    @Test
    public void testUpdatePriority_withValidIndexAndPriority() {
        // given
        int uid = 1;
        int index1 = 1;
        int index2 = 2;
        RequestPriorityDTO requestPriorityDTO = new RequestPriorityDTO(index2, index1);
        Member member = new Member();
        Goal targetGoal = new Goal(index2, member, "소비");
        targetGoal.updatePriority(2);

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(goalRepository.existsByUidAndIndex(member, index2)).thenReturn(true);
        when(goalRepository.getById(index2)).thenReturn(targetGoal);

        List<Goal> currentGoals = new ArrayList<>();
        Goal goal1 = new Goal(index1, member, "소비");
        goal1.updatePriority(1);
        currentGoals.add(goal1);
        currentGoals.add(targetGoal);

        when(goalRepository.findByUidAndCategoryAndIsAchive(member, "소비", false)).thenReturn(currentGoals);
        when(goalRepository.countByUidAndCategoryAndIsAchive(member, "소비", false)).thenReturn(currentGoals.size());

        // when
        ResponseUpdateAchiveDTO result = goalService.updatePriority(uid, requestPriorityDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getIndex()).isEqualTo(index2);
        assertThat(result.getPriority()).isEqualTo(index1);
        verify(goalRepository).saveAll(currentGoals);
        assertThat(goal1.getPriority()).isEqualTo(2);
    }

    @Test
    public void testUpdatePriority_withInvalidIndex() {
        // given
        int uid = 1;
        int index = 100;
        int newPriority = 1;
        RequestPriorityDTO requestPriorityDTO = new RequestPriorityDTO(index, newPriority);
        Member member = new Member();

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(goalRepository.existsByUidAndIndex(member, index)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> goalService.updatePriority(uid, requestPriorityDTO))
                .isInstanceOf(GoalException.class)
                .hasMessage("존재하지 않는 목표 입니다.")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_INDEX);
    }

    @Test
    public void testUpdatePriority_withInvalidPriority() {
        // given
        int uid = 1;
        int index = 100;
        int invalidPriority = 5; // Assuming currentGoalCount is less than 5
        RequestPriorityDTO requestPriorityDTO = new RequestPriorityDTO(index, invalidPriority);
        Member member = new Member();
        Goal targetGoal = new Goal(index, member, "소비");

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(goalRepository.existsByUidAndIndex(member, index)).thenReturn(true);
        when(goalRepository.getById(index)).thenReturn(targetGoal);

        List<Goal> currentGoals = new ArrayList<>();
        when(goalRepository.findByUidAndCategoryAndIsAchive(member, "소비", false)).thenReturn(currentGoals);
        when(goalRepository.countByUidAndCategoryAndIsAchive(member, "소비", false)).thenReturn(2);

        // when & then
        assertThatThrownBy(() -> goalService.updatePriority(uid, requestPriorityDTO))
                .isInstanceOf(GoalException.class)
                .hasMessage("우선 순위 값을 제대로 설정해주세요")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_PRIORITY);
    }

    @Test
    public void testGetOutcomeGoals() {
        // given
        int uid = 1;
        Member member = new Member();
        when(memberService.findMemberByUid(uid)).thenReturn(member);

        List<Goal> outcomeGoals = new ArrayList<>();
        Goal goal1 = new Goal(member, "소비", "Goal 1", 1000L, 1);

        Goal goal2 = new Goal(member, "소비", "Goal 2", 2000L, 2);

        outcomeGoals.add(goal1);
        outcomeGoals.add(goal2);

        when(goalRepository.findByUidAndCategoryAndIsAchive(member, "소비", false)).thenReturn(outcomeGoals);

        //3000 만큼 모았다.
        FinancialSumDTO oldDateAssetSum = new FinancialSumDTO(5000L);
        FinancialSumDTO todayAssetSum = new FinancialSumDTO(8000L);

        when(financeService.getAssetSummeryByDateBefore(uid, goal1.getDate())).thenReturn(oldDateAssetSum);
        when(financeService.getAssetSummeryByDateBefore(uid, any(Timestamp.class))).thenReturn(
                todayAssetSum);

        // when
        List<OutcomeGoalDTO> result = goalService.getOutcomeGoals(uid);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        OutcomeGoalDTO dto1 = result.get(0);
        assertThat(dto1.getIndex()).isEqualTo(1);
        assertThat(dto1.getGather()).isEqualTo(1000L); // Fully gathered
        assertThat(dto1.getPriority()).isEqualTo(1);

        OutcomeGoalDTO dto2 = result.get(1);
        assertThat(dto2.getIndex()).isEqualTo(2);
        assertThat(dto2.getGather()).isEqualTo(1000); // Remaining amount
        assertThat(dto2.getPriority()).isEqualTo(2);
    }

    @Test
    public void testGetAssetGoal_withExistingGoal() {
        // given
        int uid = 1;
        Member member = new Member();
        when(memberService.findMemberByUid(uid)).thenReturn(member);

        Goal goal = new Goal(member, "자산", "Asset Goal", 5000L, 1);

        when(goalRepository.existsByUidAndCategoryAndIsAchive(member, "자산", false)).thenReturn(true);
        when(goalRepository.findFirstByUidAndCategoryAndIsAchive(member, "자산", false)).thenReturn(goal);

        FinancialSumDTO oldDateAssetSum = new FinancialSumDTO(2000L);
        FinancialSumDTO todayAssetSum = new FinancialSumDTO(6000L);

        when(financeService.getAssetSummeryByDateBefore(uid, goal.getDate())).thenReturn(oldDateAssetSum);
        when(financeService.getAssetSummeryByDateBefore(uid, any(Timestamp.class))).thenReturn(
                todayAssetSum);
        // when
        AssetGoalDTO result = goalService.getAssetGoal(uid);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getIndex()).isEqualTo(1);
        assertThat(result.getGather()).isEqualTo(4000); // 6000 - 2000
        assertThat(result.getRemaindate()).isEqualTo(0); // Fully gathered
    }

    @Test
    public void testGetAssetGoal_withExistingGoalAndInsufficientAmount() {
        // given
        int uid = 1;
        Member member = new Member();
        when(memberService.findMemberByUid(uid)).thenReturn(member);

        Goal goal = new Goal(member, "자산", "Asset Goal", 5000L, 1);

        when(goalRepository.existsByUidAndCategoryAndIsAchive(member, "자산", false)).thenReturn(true);
        when(goalRepository.findFirstByUidAndCategoryAndIsAchive(member, "자산", false)).thenReturn(goal);

        FinancialSumDTO oldDateAssetSum = new FinancialSumDTO(2000L);
        FinancialSumDTO todayAssetSum = new FinancialSumDTO(4000L);

        when(financeService.getAssetSummeryByDateBefore(uid, goal.getDate())).thenReturn(oldDateAssetSum);
        when(financeService.getAssetSummeryByDateBefore(uid, any(Timestamp.class))).thenReturn(todayAssetSum);

        // when
        AssetGoalDTO result = goalService.getAssetGoal(uid);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getIndex()).isEqualTo(1);
        assertThat(result.getGather()).isEqualTo(2000); // 4000 - 2000
        assertThat(result.getRemaindate()).isGreaterThan(0); // Insufficient amount
    }

    @Test
    public void testGetAssetGoal_withNoExistingGoal() {
        // given
        int uid = 1;
        Member member = new Member();
        when(memberService.findMemberByUid(uid)).thenReturn(member);

        when(goalRepository.existsByUidAndCategoryAndIsAchive(member, "자산", false)).thenReturn(false);

        // when
        AssetGoalDTO result = goalService.getAssetGoal(uid);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getIndex()).isEqualTo(0); // Default value
        assertThat(result.getGather()).isEqualTo(0); // Default value
        assertThat(result.getRemaindate()).isEqualTo(0); // Default value
    }
}