package com.idle.kb_i_dle_backend.domain.finance.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.idle.kb_i_dle_backend.config.exception.CustomException;
import com.idle.kb_i_dle_backend.domain.finance.dto.BondDTO;
import com.idle.kb_i_dle_backend.domain.finance.entity.Bond;
import com.idle.kb_i_dle_backend.domain.finance.repository.AssetSummaryRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.BondRepository;
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
class BondServiceImplTest {

    @Mock
    private MemberService memberService;

    @Mock
    private BondRepository bondRepository;

    @Mock
    private AssetSummaryRepository assetSummaryRepository;

    @InjectMocks
    private BondServiceImpl bondService;

    private Integer uid;
    private Member member;

    @BeforeEach
    void setUp() {
        uid = 1;
        member = new Member();
        member.setUid(uid);
    }

    @Test
    @Transactional
    void shouldReturnBondList_whenBondsExist() {
        // Given
        Bond bond = new Bond();
        bond.setUid(member);
        List<Bond> bonds = List.of(bond);

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(bondRepository.findByUidAndDeleteDateIsNull(member)).thenReturn(bonds);

        // When
        List<BondDTO> bondList = bondService.getBondList(uid);

        // Then
        assertThat(bondList).isNotEmpty();
        verify(memberService).findMemberByUid(uid);
        verify(bondRepository).findByUidAndDeleteDateIsNull(member);
    }

    @Test
    @Transactional
    void shouldThrowException_whenNoBondsFound() {
        // Given
        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(bondRepository.findByUidAndDeleteDateIsNull(member)).thenReturn(new ArrayList<>());

        // When & Then
        assertThatThrownBy(() -> bondService.getBondList(uid))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.INVALID_BOND.getMessage());
    }

    @Test
    @Transactional
    void shouldSaveAndReturnBondDTO_whenAddingBond() throws ParseException {
        // Given
        BondDTO bondDTO = new BondDTO();
        Bond bond = new Bond();
        bond.setUid(member);
        bond.setName("국고채권 01500-5003(20-2)");
        bond.setCnt(10);

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(bondRepository.save(any(Bond.class))).thenReturn(bond);

        // When
        BondDTO result = bondService.addBond(uid, bondDTO);

        // Then
        assertThat(result).isNotNull();
        verify(bondRepository).save(any(Bond.class));
        verify(assetSummaryRepository).insertOrUpdateAssetSummary(uid);
    }

    @Test
    @Transactional
    void shouldUpdateBondCnt_whenUpdatingBond() {
        // Given
        Integer bondIndex = 1;

        Bond existingBond = new Bond();
        existingBond.setIndex(bondIndex);
        existingBond.setUid(member);
        existingBond.setCnt(10);

        BondDTO updateBondDTO = new BondDTO();
        updateBondDTO.setIndex(bondIndex);
        updateBondDTO.setCnt(20);

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(bondRepository.findByIndexAndDeleteDateIsNull(bondIndex)).thenReturn(Optional.of(existingBond));
        when(bondRepository.save(any(Bond.class))).thenReturn(existingBond);

        // When
        BondDTO result = bondService.updateBond(uid, updateBondDTO);

        // Then
        assertThat(result.getCnt()).isEqualTo(20);
        verify(bondRepository).save(any(Bond.class));
        verify(assetSummaryRepository).insertOrUpdateAssetSummary(uid);
    }

    @Test
    @Transactional
    void shouldMarkBondAsDeleted_whenDeletingBond() {
        // Given
        Integer bondIndex = 1;
        Bond bond = new Bond();
        bond.setUid(member);
        bond.setIndex(bondIndex);

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(bondRepository.findByIndexAndDeleteDateIsNull(bondIndex)).thenReturn(Optional.of(bond));
        when(bondRepository.save(any(Bond.class))).thenReturn(bond);

        // When
        BondDTO result = bondService.deleteBond(uid, bondIndex);

        // Then
        assertThat(result.getDeleteDate()).isNotNull();
        verify(bondRepository).save(any(Bond.class));
        verify(assetSummaryRepository).insertOrUpdateAssetSummary(uid);
    }
}
