package com.idle.kb_i_dle_backend.domain.outcome.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.idle.kb_i_dle_backend.domain.outcome.dto.OutcomeUserDTO;
import com.idle.kb_i_dle_backend.domain.outcome.entity.OutcomeUser;
import com.idle.kb_i_dle_backend.domain.outcome.repository.OutcomeUserRepository;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.domain.finance.repository.AssetSummaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
class OutcomeServiceImplTest {
    @Mock
    private MemberService memberService;
    @Mock
    private OutcomeUserRepository outcomeUserRepository;
    @Mock
    private AssetSummaryRepository assetSummaryRepository;
    @InjectMocks
    private OutcomeServiceImpl outcomeService;
    @Test
    void addOutcome() {
        // Given
        Integer uid = 40;
        Member member = new Member();
        member.setUid(uid);
        OutcomeUserDTO outcomeUserDTO = new OutcomeUserDTO();
        outcomeUserDTO.setExpCategory("Grocery");        outcomeUserDTO.setDate("2024-10-14");        outcomeUserDTO.setAmount(100L);        outcomeUserDTO.setMemo("Weekly groceries");        outcomeUserDTO.setDescript("Bought fruits and vegetables");
        OutcomeUser savedOutcome = new OutcomeUser();
        savedOutcome.setIndex(1);        savedOutcome.setUid(member);        savedOutcome.setCategory(outcomeUserDTO.getExpCategory());
        try {
            savedOutcome.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(outcomeUserDTO.getDate()));
        } catch (ParseException e) {            throw new RuntimeException(e);        }
        savedOutcome.setAmount(outcomeUserDTO.getAmount());        savedOutcome.setDescript(outcomeUserDTO.getDescript());        savedOutcome.setMemo(outcomeUserDTO.getMemo());
        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(outcomeUserRepository.save(any(OutcomeUser.class))).thenReturn(savedOutcome);
        // When
        OutcomeUserDTO result = null;
        try {
            result = outcomeService.addOutcome(uid, outcomeUserDTO);
        } catch (ParseException e) {            throw new RuntimeException(e);        }
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIndex()).isEqualTo(1);
        assertThat(result.getExpCategory()).isEqualTo(outcomeUserDTO.getExpCategory());
        assertThat(result.getDate()).isEqualTo(outcomeUserDTO.getDate());
        assertThat(result.getAmount()).isEqualTo(outcomeUserDTO.getAmount());
        assertThat(result.getMemo()).isEqualTo(outcomeUserDTO.getMemo());
        assertThat(result.getDescript()).isEqualTo(outcomeUserDTO.getDescript());
        verify(memberService).findMemberByUid(uid);
        verify(outcomeUserRepository).save(any(OutcomeUser.class));
        verify(assetSummaryRepository).insertOrUpdateAssetSummary(uid);
    }
}