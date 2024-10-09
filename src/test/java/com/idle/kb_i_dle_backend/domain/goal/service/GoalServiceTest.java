package com.idle.kb_i_dle_backend.domain.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.idle.kb_i_dle_backend.domain.goal.dto.AddGoalDTO;
import com.idle.kb_i_dle_backend.domain.goal.dto.GoalDTO;
import com.idle.kb_i_dle_backend.domain.goal.entity.Goal;
import com.idle.kb_i_dle_backend.domain.goal.repository.GoalRepository;
import com.idle.kb_i_dle_backend.domain.goal.service.Impl.GoalServiceImpl;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.service.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GoalServiceTest {

    @InjectMocks
    private GoalServiceImpl goalService; // 서비스 클래스 이름

    @Mock
    private MemberServiceImpl memberService;

    @Mock
    private GoalRepository goalRepository; // 가정: 목표를 저장하는 레포지토리

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveOutcomeGoal_shouldSaveGoalSuccessfully() {
        // given
        Member member = new Member(); // 가정: Member 객체 생성
        AddGoalDTO addGoalDTO = new AddGoalDTO();
        addGoalDTO.setCategory("소비");
        addGoalDTO.setTitle("에어팟");
        addGoalDTO.setAmount(10000L);

        // 현재 목표 개수 설정
        when(goalRepository.countByUidAndCategoryAndIsAchive(member, "소비", false)).thenReturn(2);

        Goal mockGoal = new Goal(member, addGoalDTO.getCategory(), addGoalDTO.getTitle(), addGoalDTO.getAmount(),
                3); // 목표 생성
        when(goalRepository.save(any(Goal.class))).thenReturn(mockGoal); // save 메서드가 호출될 때 mockGoal을 반환

        // when
        GoalDTO result = goalService.saveOutcomeGoal(member, addGoalDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCategory()).isEqualTo("소비");
        assertThat(result.getTitle()).isEqualTo("에어팟");
        assertThat(result.getAmount()).isEqualTo(10000L);
        assertThat(result.getPriority()).isEqualTo(3); // 우선순위가 3임을 검증
    }

    @Test
    void saveOutcomeGoal() {
    }

    @Test
    void saveAssetGoal() {
    }

    @Test
    void updateAchive() {
    }

    @Test
    void removeGoal() {
    }

    @Test
    void updatePriority() {
    }

    @Test
    void getOutcomeGoals() {
    }

    @Test
    void getAssetGoal() {
    }
}