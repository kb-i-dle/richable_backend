package com.idle.kb_i_dle_backend.domain.outcome.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.idle.kb_i_dle_backend.config.exception.CustomException;
import com.idle.kb_i_dle_backend.domain.finance.repository.AssetSummaryRepository;
import com.idle.kb_i_dle_backend.domain.income.service.IncomeService;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.repository.MemberRepository;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.domain.outcome.dto.CategorySumDTO;
import com.idle.kb_i_dle_backend.domain.outcome.dto.CompareAverageCategoryOutcomeDTO;
import com.idle.kb_i_dle_backend.domain.outcome.dto.MonthOutcomeDTO;
import com.idle.kb_i_dle_backend.domain.outcome.dto.OutcomeUserDTO;
import com.idle.kb_i_dle_backend.domain.outcome.dto.ResponseCategorySumListDTO;
import com.idle.kb_i_dle_backend.domain.outcome.entity.OutcomeAverage;
import com.idle.kb_i_dle_backend.domain.outcome.entity.OutcomeCategory;
import com.idle.kb_i_dle_backend.domain.outcome.entity.OutcomeUser;
import com.idle.kb_i_dle_backend.domain.outcome.repository.AverageOutcomeRepository;
import com.idle.kb_i_dle_backend.domain.outcome.repository.CategoryRepository;
import com.idle.kb_i_dle_backend.domain.outcome.repository.OutcomeUserRepository;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.javassist.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class OutcomeServiceImplTest {

    @Mock
    private AverageOutcomeRepository averageOutcomeRepository;

    @Mock
    private OutcomeUserRepository outcomeUserRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private IncomeService incomeService;

    @Mock
    private AssetSummaryRepository assetSummaryRepository;

    @InjectMocks
    private OutcomeServiceImpl outcomeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        //OutcomeServiceImpl 내부의 private함수 실행을 위해서
        outcomeService = spy(new OutcomeServiceImpl(
                averageOutcomeRepository,
                outcomeUserRepository,
                categoryRepository,
                memberRepository,
                memberService,
                incomeService,
                assetSummaryRepository
        ));
    }

    @Test
    public void testFindCategorySum() {
        // given
        int uid = 1;
        int year = 2023;
        int month = 10;
        Member member = new Member();
        when(memberService.findMemberByUid(uid)).thenReturn(member);

        List<CategorySumDTO> categorySumDTOS = Arrays.asList(
                new CategorySumDTO("식료품 · 비주류음료", 1500L),
                new CategorySumDTO("주류 · 담배", 800L),
                new CategorySumDTO("의류 · 신발", 1200L)
        );

        when(outcomeUserRepository.findCategorySumByUidAndYearAndMonth(member, year, month)).thenReturn(
                categorySumDTOS);

        // when
        ResponseCategorySumListDTO result = outcomeService.findCategorySum(uid, year, month);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCategorys()).hasSize(3);
        assertThat(result.getCategorys()).extracting("category").containsExactly("식료품 · 비주류음료", "주류 · 담배", "의류 · 신발");
        assertThat(result.getCategorys()).extracting("sum").containsExactly(1500L, 800L, 1200L);
        assertThat(result.getSum()).isEqualTo(3500L);
    }


    @Test
    public void testFindMonthOutcome() {
        // given
        int uid = 1;
        int year = 2023;
        int month = 10;
        Member member = new Member();
        when(memberService.findMemberByUid(uid)).thenReturn(member);

        //생성자 수정 필요할듯
        List<OutcomeUser> consumes = Arrays.asList(
                new OutcomeUser(member, "식료품 · 비주류음료", new Date(2023 - 1900, 9, 1), 100L),
                // Note: Month is 0-based in Date
                new OutcomeUser(member, "주류 · 담배", new Date(2023 - 1900, 9, 2), 200L),
                new OutcomeUser(member, "의류 · 신발", new Date(2023 - 1900, 9, 2), 300L)
        );

        when(outcomeUserRepository.findAmountAllByUidAndYearAndMonth(member, year, month)).thenReturn(consumes);

        // when
        MonthOutcomeDTO result = outcomeService.findMonthOutcome(uid, year, month);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCntMonth()).isEqualTo(month);
        assertThat(result.getCntYear()).isEqualTo(year);

        List<Long> expectedDailyAmount = new ArrayList<>(Arrays.asList(
                100L, 600L, 600L, 600L, 600L, 600L, 600L, 600L, 600L, 600L, 600L, 600L, 600L, 600L, 600L, 600L, 600L,
                600L, 600L, 600L, 600L, 600L, 600L, 600L, 600L, 600L, 600L, 600L, 600L, 600L, 600L
        ));

        assertThat(result.getPrices()).isEqualTo(expectedDailyAmount);
    }

    @Test
    public void testCompareWithAverage() {
        // given
        int uid = 1;
        int year = 2023;
        int month = 10;
        String category = "식료품 · 비주류음료";
        Member member = new Member();
        member.setBirth_year(1990);
        when(memberService.findMemberByUid(uid)).thenReturn(member);

        //category
        OutcomeCategory outcomeCategory = new OutcomeCategory();
        outcomeCategory.setCategoryName(category);
        when(categoryRepository.findByCategoryNameStartingWith(category)).thenReturn(outcomeCategory);

        //outcomeUser
        List<OutcomeUser> outcomeUsers = Collections.emptyList();
        when(outcomeUserRepository.findAllByUidAndYearAndMonthAndCategory(member, year, month, category)).thenReturn(
                outcomeUsers);

        //outcomeAverage
        List<OutcomeAverage> outcomeAverages = Collections.emptyList();
        when(averageOutcomeRepository.findByCategoryAndQuaterAndAgeGroup(eq(category), anyString(),
                anyString())).thenReturn(
                outcomeAverages);

        CompareAverageCategoryOutcomeDTO expectedDTO = new CompareAverageCategoryOutcomeDTO();
        doReturn(expectedDTO).when(outcomeService)
                .getCompareAverageCategoryOutcomeDTO(eq(member), eq(year), eq(month), eq(category), anyString(),
                        any(CompareAverageCategoryOutcomeDTO.class));

        // when
        CompareAverageCategoryOutcomeDTO result = outcomeService.compareWithAverage(uid, year, month, category);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedDTO);
    }

    @Test
    public void testGetOutcomeList_withResults() throws Exception {
        // given
        int uid = 1;
        Member member = new Member();
        when(memberService.findMemberByUid(uid)).thenReturn(member);

        OutcomeUser outcome1 = new OutcomeUser();
        outcome1.setIndex(1);
        outcome1.setCategory("식료품 · 비주류음료");
        outcome1.setDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-10-01"));
        outcome1.setAmount(1000L);
        outcome1.setDescript("Description 1");
        outcome1.setMemo("Memo 1");
        outcome1.setAccountNum(123456L);

        OutcomeUser outcome2 = new OutcomeUser();
        outcome2.setIndex(2);
        outcome2.setCategory("주류 · 담배");
        outcome2.setDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-10-02"));
        outcome2.setAmount(2000L);
        outcome2.setDescript("Description 2");
        outcome2.setMemo("Memo 2");
        outcome2.setAccountNum(654321L);

        List<OutcomeUser> outcomes = Arrays.asList(outcome1, outcome2);
        when(outcomeUserRepository.findByUid(member)).thenReturn(outcomes);

        // when
        List<OutcomeUserDTO> result = outcomeService.getOutcomeList(uid);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        OutcomeUserDTO dto1 = OutcomeUserDTO.convertToDTO(outcome1);
        OutcomeUserDTO dto2 = OutcomeUserDTO.convertToDTO(outcome2);

        assertThat(result).containsExactly(dto1, dto2);
    }

    @Test
    public void testGetOutcomeList_withNoResults() {
        // given
        int uid = 1;
        Member member = new Member();
        when(memberService.findMemberByUid(uid)).thenReturn(member);

        when(outcomeUserRepository.findByUid(member)).thenReturn(Arrays.asList());

        // when & then
        assertThatThrownBy(() -> outcomeService.getOutcomeList(uid))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("");
    }

    @Test
    public void testGetOutcomeByIndex_withValidData() throws Exception {
        // given
        int uid = 1;
        int index = 100;
        Member member = new Member();
        member.setUid(uid);
        when(memberService.findMemberByUid(uid)).thenReturn(member);

        OutcomeUser outcomeUser = new OutcomeUser();
        outcomeUser.setIndex(index);
        outcomeUser.setUid(member);
        outcomeUser.setCategory("식료품 · 비주류음료");
        outcomeUser.setDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-10-01"));
        outcomeUser.setAmount(1000L);
        outcomeUser.setDescript("Description");
        outcomeUser.setMemo("Memo");
        outcomeUser.setAccountNum(123456L);

        when(outcomeUserRepository.findByIndex(index)).thenReturn(Optional.of(outcomeUser));

        // when
        OutcomeUserDTO result = outcomeService.getOutcomeByIndex(uid, index);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getIndex()).isEqualTo(index);
    }

    @Test
    public void testGetOutcomeByIndex_withInvalidIndex() {
        // given
        int uid = 1;
        int index = 100;
        Member member = new Member();
        member.setUid(uid);
        when(memberService.findMemberByUid(uid)).thenReturn(member);

        when(outcomeUserRepository.findByIndex(index)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> outcomeService.getOutcomeByIndex(uid, index))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("Outcome not found with index: " + index);
    }

    @Test
    public void testGetOutcomeByIndex_withInvalidOwner() {
        // given
        int uid = 1;
        int index = 100;
        Member member = new Member();
        member.setUid(uid);
        when(memberService.findMemberByUid(uid)).thenReturn(member);

        Member otherMember = new Member();
        otherMember.setUid(2); // 다른 사용자
        OutcomeUser outcomeUser = new OutcomeUser();
        outcomeUser.setIndex(index);
        outcomeUser.setUid(otherMember);

        when(outcomeUserRepository.findByIndex(index)).thenReturn(Optional.of(outcomeUser));

        // when & then
        assertThatThrownBy(() -> outcomeService.getOutcomeByIndex(uid, index))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("You do not have permission to delete this outcome.");
    }

    @Test
    public void testAddOutcome() throws ParseException {
        // given
        int uid = 1;
        Member member = new Member();
        member.setUid(uid);
        when(memberService.findMemberByUid(uid)).thenReturn(member);

        OutcomeUserDTO outcomeUserDTO = new OutcomeUserDTO();
        outcomeUserDTO.setIndex(1);
        outcomeUserDTO.setExpCategory("식료품 · 비주류음료");
        outcomeUserDTO.setDate("2023-10-01");
        outcomeUserDTO.setAmount(1000L);
        outcomeUserDTO.setDescript("Description");
        outcomeUserDTO.setMemo("Memo");
        outcomeUserDTO.setAccountNum(123456L);

        OutcomeUser outcomeUser = new OutcomeUser();
        outcomeUser.setIndex(1);
        outcomeUser.setUid(member);
        outcomeUser.setCategory("식료품 · 비주류음료");
        outcomeUser.setDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-10-01"));
        outcomeUser.setAmount(1000L);
        outcomeUser.setDescript("Description");
        outcomeUser.setMemo("Memo");
        outcomeUser.setAccountNum(123456L);

        when(outcomeUserRepository.save(any(OutcomeUser.class))).thenReturn(outcomeUser);

        // when
        OutcomeUserDTO result = outcomeService.addOutcome(uid, outcomeUserDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getIndex()).isEqualTo(1);
        assertThat(result.getExpCategory()).isEqualTo("식료품 · 비주류음료");
        assertThat(result.getDate()).isEqualTo("2023-10-01");
        assertThat(result.getAmount()).isEqualTo(1000L);
        assertThat(result.getDescript()).isEqualTo("Description");
        assertThat(result.getMemo()).isEqualTo("Memo");
        assertThat(result.getAccountNum()).isEqualTo(123456L);

        // Verify that asset summary is updated
        verify(assetSummaryRepository).insertOrUpdateAssetSummary(uid);
    }

    @Test
    public void testUpdateOutcome_withValidData() throws ParseException {
        // given
        int uid = 1;
        int index = 100;
        Member member = new Member();
        member.setUid(uid);
        when(memberService.findMemberByUid(uid)).thenReturn(member);

        OutcomeUserDTO outcomeUserDTO = new OutcomeUserDTO();
        outcomeUserDTO.setIndex(index);
        outcomeUserDTO.setExpCategory("식료품 · 비주류음료");
        outcomeUserDTO.setDate("2023-10-01");
        outcomeUserDTO.setAmount(1500L);
        outcomeUserDTO.setDescript("Updated Description");
        outcomeUserDTO.setMemo("Updated Memo");

        OutcomeUser existingOutcomeUser = new OutcomeUser();
        existingOutcomeUser.setIndex(index);
        existingOutcomeUser.setUid(member);

        when(outcomeUserRepository.findByIndex(index)).thenReturn(Optional.of(existingOutcomeUser));
        when(memberRepository.findByUid(uid)).thenReturn(member);

        OutcomeUser updatedOutcomeUser = new OutcomeUser();
        updatedOutcomeUser.setIndex(index);
        updatedOutcomeUser.setUid(member);
        updatedOutcomeUser.setCategory("식료품 · 비주류음료");
        updatedOutcomeUser.setDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-10-01"));
        updatedOutcomeUser.setAmount(1500L);
        updatedOutcomeUser.setDescript("Updated Description");
        updatedOutcomeUser.setMemo("Updated Memo");

        when(outcomeUserRepository.save(existingOutcomeUser)).thenReturn(updatedOutcomeUser);

        // when
        OutcomeUserDTO result = outcomeService.updateOutcome(uid, outcomeUserDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getIndex()).isEqualTo(index);
        assertThat(result.getExpCategory()).isEqualTo("식료품 · 비주류음료");
        assertThat(result.getDate()).isEqualTo("2023-10-01");
        assertThat(result.getAmount()).isEqualTo(1500L);
        assertThat(result.getDescript()).isEqualTo("Updated Description");
        assertThat(result.getMemo()).isEqualTo("Updated Memo");

        // Verify that asset summary is updated
        verify(assetSummaryRepository).insertOrUpdateAssetSummary(uid);
    }

    @Test
    public void testUpdateOutcome_withInvalidIndex() {
        // given
        int uid = 1;
        int index = 100;
        Member member = new Member();
        member.setUid(uid);
        when(memberService.findMemberByUid(uid)).thenReturn(member);

        OutcomeUserDTO outcomeUserDTO = new OutcomeUserDTO();
        outcomeUserDTO.setIndex(index);

        when(outcomeUserRepository.findByIndex(index)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> outcomeService.updateOutcome(uid, outcomeUserDTO))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("Outcome not found with id: " + index);
    }

    @Test
    public void testUpdateOutcome_withInvalidOwner() {
        // given
        int uid = 1;
        int index = 100;
        Member member = new Member();
        member.setUid(uid);
        when(memberService.findMemberByUid(uid)).thenReturn(member);

        OutcomeUserDTO outcomeUserDTO = new OutcomeUserDTO();
        outcomeUserDTO.setIndex(index);

        Member otherMember = new Member();
        otherMember.setUid(2); // 다른 사용자
        OutcomeUser existingOutcomeUser = new OutcomeUser();
        existingOutcomeUser.setIndex(index);
        existingOutcomeUser.setUid(otherMember);

        when(outcomeUserRepository.findByIndex(index)).thenReturn(Optional.of(existingOutcomeUser));
        when(memberRepository.findByUid(uid)).thenReturn(member);

        // when & then
        assertThatThrownBy(() -> outcomeService.updateOutcome(uid, outcomeUserDTO))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("You do not have permission to modify this outcome.");
    }

    @Test
    public void testDeleteOutcomeByUidAndIndex_withValidData() {
        // given
        int uid = 1;
        int index = 100;
        Member member = new Member();
        member.setUid(uid);
        when(memberService.findMemberByUid(uid)).thenReturn(member);

        OutcomeUser outcomeUser = new OutcomeUser();
        outcomeUser.setIndex(index);
        outcomeUser.setUid(member);

        when(outcomeUserRepository.findByIndex(index)).thenReturn(Optional.of(outcomeUser));

        // when
        Integer result = outcomeService.deleteOutcomeByUidAndIndex(uid, index);

        // then
        assertThat(result).isEqualTo(index);

        // Verify that the outcome was deleted and asset summary was updated
        verify(outcomeUserRepository).deleteByIndex(index);
        verify(assetSummaryRepository).insertOrUpdateAssetSummary(uid);
    }

    @Test
    public void testDeleteOutcomeByUidAndIndex_withInvalidIndex() {
        // given
        int uid = 1;
        int index = 100;
        Member member = new Member();
        member.setUid(uid);
        when(memberService.findMemberByUid(uid)).thenReturn(member);

        when(outcomeUserRepository.findByIndex(index)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> outcomeService.deleteOutcomeByUidAndIndex(uid, index))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("Outcome not found with index: " + index);
    }

    @Test
    public void testDeleteOutcomeByUidAndIndex_withInvalidOwner() {
        // given
        int uid = 1;
        int index = 100;
        Member member = new Member();
        member.setUid(uid);
        when(memberService.findMemberByUid(uid)).thenReturn(member);

        Member otherMember = new Member();
        otherMember.setUid(2); // 다른 사용자
        OutcomeUser outcomeUser = new OutcomeUser();
        outcomeUser.setIndex(index);
        outcomeUser.setUid(otherMember);

        when(outcomeUserRepository.findByIndex(index)).thenReturn(Optional.of(outcomeUser));

        // when & then
        assertThatThrownBy(() -> outcomeService.deleteOutcomeByUidAndIndex(uid, index))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("You do not have permission to delete this outcome.");
    }

}