package com.idle.kb_i_dle_backend.domain.finance.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.idle.kb_i_dle_backend.config.exception.CustomException;
import com.idle.kb_i_dle_backend.domain.finance.dto.PriceSumDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.SpotDTO;
import com.idle.kb_i_dle_backend.domain.finance.entity.Spot;
import com.idle.kb_i_dle_backend.domain.finance.repository.SpotRepository;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.global.codes.ErrorCode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
class SpotServiceImplTest {

    private final Integer uid = 1;
    private final Member member = createMember(uid);

    @Mock
    private SpotRepository spotRepository;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private SpotServiceImpl spotService;

    @Test
    @Transactional
    void shouldReturnTotalPriceByCategory_whenSpotsExist() {
        // Given
        String category = "car";
        List<Spot> spots = List.of(createSpot(member, "자동차", 1000L));

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(spotRepository.findByUidAndCategoryAndDeleteDateIsNull(member, "자동차")).thenReturn(spots);

        // When
        PriceSumDTO priceSumDTO = spotService.getTotalPriceByCategory(uid, category);

        // Then
        assertThat(priceSumDTO.getProdCategory()).isEqualTo("car");
        assertThat(priceSumDTO.getAmount()).isEqualTo(1000L);
    }

    @Test
    @Transactional
    void shouldThrowException_whenNoSpotsExistInCategory() {
        // Given
        String category = "luxury";
        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(spotRepository.findByUidAndCategoryAndDeleteDateIsNull(member, "명품")).thenReturn(Collections.emptyList());

        // When & Then
        assertThatThrownBy(() -> spotService.getTotalPriceByCategory(uid, category))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.INVALID_SPOT.getMessage());
    }

    @Test
    @Transactional
    void shouldReturnTotalPrice_whenSpotsExist() {
        // Given
        List<Spot> spots = List.of(createSpot(member, "기타", 500L));

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(spotRepository.findByUidAndDeleteDateIsNull(member)).thenReturn(spots);

        // When
        PriceSumDTO totalPrice = spotService.getTotalPrice(uid);

        // Then
        assertThat(totalPrice.getProdCategory()).isEqualTo("현물자산");
        assertThat(totalPrice.getAmount()).isEqualTo(500L);
    }

    @Test
    @Transactional
    void shouldThrowException_whenNoSpotsExistForTotalPrice() {
        // Given
        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(spotRepository.findByUidAndDeleteDateIsNull(member)).thenReturn(Collections.emptyList());

        // When & Then
        assertThatThrownBy(() -> spotService.getTotalPrice(uid))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.INVALID_SPOT.getMessage());
    }

    @Test
    @Transactional
    void shouldReturnSpotList_whenSpotsExist() {
        // Given
        Spot spot = createSpot(member, "전자기기", 2000L);
        List<Spot> spots = List.of(spot);

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(spotRepository.findByUidAndDeleteDateIsNull(member)).thenReturn(spots);

        // When
        List<SpotDTO> spotList = spotService.getSpotList(uid);

        // Then
        assertThat(spotList).isNotEmpty();
        assertThat(spotList.get(0).getCategory()).isEqualTo("전자기기");
    }

    @Test
    @Transactional
    void shouldAddSpotSuccessfully() throws ParseException {
        // Given
        SpotDTO spotDTO = new SpotDTO();
        spotDTO.setCategory("명품");
        spotDTO.setPrice(1500L);
        Spot spot = createSpot(member, "명품", 1500L);

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(spotRepository.save(any(Spot.class))).thenReturn(spot);

        // When
        SpotDTO result = spotService.addSpot(uid, spotDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCategory()).isEqualTo("명품");
        assertThat(result.getPrice()).isEqualTo(1500L);
    }

    @Test
    @Transactional
    void shouldUpdateSpotSuccessfully() {
        // Given
        Spot spot = createSpot(member, "기타", 1200L);
        SpotDTO spotDTO = new SpotDTO();
        spotDTO.setIndex(1);
        spotDTO.setName("New Spot Name");
        spotDTO.setPrice(2200L);

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(spotRepository.findByIndexAndDeleteDateIsNull(spotDTO.getIndex())).thenReturn(Optional.of(spot));
        when(spotRepository.save(any(Spot.class))).thenReturn(spot);

        // When
        SpotDTO result = spotService.updateSpot(uid, spotDTO);

        // Then
        assertThat(result.getPrice()).isEqualTo(2200L);
    }

    @Test
    @Transactional
    void shouldMarkSpotAsDeleted_whenDeletingSpot() {
        // Given
        Spot spot = createSpot(member, "기타", 1200L);
        spot.setIndex(1);

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(spotRepository.findByIndexAndDeleteDateIsNull(spot.getIndex())).thenReturn(Optional.of(spot));
        when(spotRepository.save(any(Spot.class))).thenReturn(spot);

        // When
        SpotDTO result = spotService.deleteSpot(uid, spot.getIndex());

        // Then
        assertThat(result.getDeleteDate()).isNotNull();
    }

    // Helper Methods
    private Member createMember(Integer uid) {
        Member member = new Member();
        member.setUid(uid);
        return member;
    }

    private Spot createSpot(Member member, String category, Long price) {
        Spot spot = new Spot();
        spot.setUid(member);
        spot.setCategory(category);
        spot.setName("Test Spot");
        spot.setPrice(price);
        spot.setAddDate(new Date());
        return spot;
    }
}
