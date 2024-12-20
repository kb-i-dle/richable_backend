package com.idle.kb_i_dle_backend.domain.income.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.idle.kb_i_dle_backend.domain.income.dto.IncomeDTO;
import com.idle.kb_i_dle_backend.domain.income.entity.Income;
import com.idle.kb_i_dle_backend.domain.income.repository.IncomeRepository;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.domain.finance.repository.AssetSummaryRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
class IncomeServiceImplTest {
    @Mock
    private MemberService memberService;

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private AssetSummaryRepository assetSummaryRepository;

    @InjectMocks
    private IncomeServiceImpl incomeService;
    @Test
    void addIncome() throws ParseException {
        // Given
        Integer uid = 40;
        Member member = new Member();
        member.setUid(uid);

        IncomeDTO incomeDTO = new IncomeDTO();
        incomeDTO.setType("월급");        incomeDTO.setIncomeDate("2024-10-14");        incomeDTO.setPrice(5000L);        incomeDTO.setContents("월급");        incomeDTO.setMemo("10월 월급");
        Income savedIncome = new Income();

        savedIncome.setIndex(1);        savedIncome.setUid(member);        savedIncome.setType(incomeDTO.getType());        savedIncome.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(incomeDTO.getIncomeDate()));
        savedIncome.setAmount(incomeDTO.getPrice());        savedIncome.setDescript(incomeDTO.getContents());        savedIncome.setMemo(incomeDTO.getMemo());

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(incomeRepository.save(any(Income.class))).thenReturn(savedIncome);

        // When
        IncomeDTO result = incomeService.addIncome(uid, incomeDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIncomeId()).isEqualTo(1);
        assertThat(result.getType()).isEqualTo(incomeDTO.getType());
        assertThat(result.getIncomeDate()).isEqualTo(incomeDTO.getIncomeDate());
        assertThat(result.getPrice()).isEqualTo(incomeDTO.getPrice());
        assertThat(result.getContents()).isEqualTo(incomeDTO.getContents());
        assertThat(result.getMemo()).isEqualTo(incomeDTO.getMemo());

        verify(memberService).findMemberByUid(uid);
        verify(incomeRepository).save(any(Income.class));
        verify(assetSummaryRepository).insertOrUpdateAssetSummary(uid);
    }

    @Test
    void updateIncome_shouldUpdateAmountCorrectly() throws ParseException {
        // Given
        Integer uid = 40;
        Integer incomeId = 1;
        Member member = new Member();
        member.setUid(uid);

        Income existingIncome = new Income();
        existingIncome.setIndex(incomeId);
        existingIncome.setUid(member);
        existingIncome.setType("월급");
        existingIncome.setDate(new SimpleDateFormat("yyyy-MM-dd").parse("2024-10-14"));
        existingIncome.setAmount(5000L);
        existingIncome.setDescript("월급");
        existingIncome.setMemo("10월 월급");

        IncomeDTO updateIncomeDTO = new IncomeDTO();
        updateIncomeDTO.setIncomeId(incomeId);
        updateIncomeDTO.setType("월급");
        updateIncomeDTO.setIncomeDate("2024-10-14");
        updateIncomeDTO.setPrice(7000L); // 2000 increase
        updateIncomeDTO.setContents("월급");
        updateIncomeDTO.setMemo("10월 월급 내역 수정");

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(incomeRepository.findByIndex(incomeId)).thenReturn(Optional.of(existingIncome));
        when(incomeRepository.save(any(Income.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        IncomeDTO result = incomeService.updateIncome(uid, updateIncomeDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPrice()).isEqualTo(7000L);
//        assertThat(result.getPrice()).isEqualTo(existingIncome.getAmount() + 2000L);
        assertThat(result.getMemo()).isEqualTo("10월 월급 내역 수정");

        verify(memberService).findMemberByUid(uid);
        verify(incomeRepository).findByIndex(incomeId);
        verify(incomeRepository).save(any(Income.class));
        verify(assetSummaryRepository).insertOrUpdateAssetSummary(uid);
    }

}