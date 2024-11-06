package com.idle.kb_i_dle_backend.domain.finance.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.idle.kb_i_dle_backend.config.exception.CustomException;
import com.idle.kb_i_dle_backend.domain.finance.dto.BankDTO;
import com.idle.kb_i_dle_backend.domain.finance.entity.Bank;
import com.idle.kb_i_dle_backend.domain.finance.repository.AssetSummaryRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.BankRepository;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.global.codes.ErrorCode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
class BankServiceImplTest {

    @Mock
    private MemberService memberService;

    @Mock
    private BankRepository bankRepository;

    @Mock
    private AssetSummaryRepository assetSummaryRepository;

    @InjectMocks
    private BankServiceImpl bankService;

    private final Integer uid = 1;
    private final Member member = new Member();

    @BeforeEach
    void setUp() {
        member.setUid(uid);
        when(memberService.findMemberByUid(uid)).thenReturn(member);
    }

    @Test
    @Transactional
    void getBankList_ShouldReturnBankList() {
        // Given
        List<Bank> banks = List.of(new Bank());
        when(bankRepository.findByUidAndDeleteDateIsNull(member)).thenReturn(banks);

        // When
        List<BankDTO> bankList = bankService.getBankList(uid);

        // Then
        assertThat(bankList).isNotEmpty();
        verify(memberService).findMemberByUid(uid);
        verify(bankRepository).findByUidAndDeleteDateIsNull(member);
    }

    @Test
    @Transactional
    void getBankList_ShouldThrowExceptionIfNoBanks() {
        // Given
        when(bankRepository.findByUidAndDeleteDateIsNull(member)).thenReturn(new ArrayList<>());

        // When & Then
        assertThatThrownBy(() -> bankService.getBankList(uid))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.INVALID_BANK.getMessage());
    }

    @Test
    @Transactional
    void addBank_ShouldSaveAndReturnBankDTO() throws ParseException {
        // Given
        Integer uid = 1;
        BankDTO bankDTO = new BankDTO();
        bankDTO.setProdCategory("예금");
        bankDTO.setBalanceAmt(10000L);
        Member member = new Member();
        member.setUid(uid);

        Bank bank = new Bank();
        bank.setUid(member);
        bank.setCategory("예금");
        bank.setBalanceAmt(10000L);
        bank.setIndex(1); // NullPointerException 방지

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(bankRepository.save(any(Bank.class))).thenReturn(bank);

        // When
        BankDTO result = bankService.addBank(uid, bankDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getProdCategory()).isEqualTo("예금");
        assertThat(result.getBalanceAmt()).isEqualTo(10000L);
        verify(memberService).findMemberByUid(uid);
        verify(bankRepository).save(any(Bank.class));
        verify(assetSummaryRepository).insertOrUpdateAssetSummary(uid);
    }



    @Test
    @Transactional
    void updateBank_ShouldUpdateBalanceAmt() {
        // Given
        Bank bank = new Bank();
        bank.setUid(member);
        bank.setBalanceAmt(1000L);
        BankDTO bankDTO = new BankDTO();
        bankDTO.setIndex(1);
        bankDTO.setBalanceAmt(2000L);

        when(bankRepository.findByIndexAndDeleteDateIsNull(bankDTO.getIndex())).thenReturn(Optional.of(bank));
        when(bankRepository.save(any(Bank.class))).thenReturn(bank);

        // When
        BankDTO result = bankService.updateBank(uid, bankDTO);

        // Then
        assertThat(result.getBalanceAmt()).isEqualTo(2000L);
        verify(bankRepository).save(any(Bank.class));
        verify(assetSummaryRepository).insertOrUpdateAssetSummary(uid);
    }

    @Test
    @Transactional
    void deleteBank_ShouldMarkBankAsDeleted() {
        // Given
        Bank bank = new Bank();
        bank.setUid(member);
        bank.setIndex(1);

        when(bankRepository.findByIndexAndDeleteDateIsNull(bank.getIndex())).thenReturn(Optional.of(bank));
        when(bankRepository.save(any(Bank.class))).thenReturn(bank);

        // When
        BankDTO result = bankService.deleteBank(uid, bank.getIndex());

        // Then
        assertThat(result.getDeleteDate()).isNotNull();
        verify(bankRepository).save(any(Bank.class));
        verify(assetSummaryRepository).insertOrUpdateAssetSummary(uid);
    }

    @Test
    @Transactional
    void getAccount_ShouldReturnAccountBankList() {
        // Given
        List<Bank> banks = List.of(new Bank());
        when(bankRepository.findByUidAndSpecificCategoriesAndDeleteDateIsNull(member)).thenReturn(banks);

        // When
        List<BankDTO> accountList = bankService.getAccount(uid);

        // Then
        assertThat(accountList).isNotEmpty();
        verify(bankRepository).findByUidAndSpecificCategoriesAndDeleteDateIsNull(member);
    }
}
