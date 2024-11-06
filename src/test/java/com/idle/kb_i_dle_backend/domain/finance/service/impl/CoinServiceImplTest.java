package com.idle.kb_i_dle_backend.domain.finance.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.idle.kb_i_dle_backend.config.exception.CustomException;
import com.idle.kb_i_dle_backend.domain.finance.dto.CoinDTO;
import com.idle.kb_i_dle_backend.domain.finance.entity.Coin;
import com.idle.kb_i_dle_backend.domain.finance.repository.AssetSummaryRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.CoinRepository;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.global.codes.ErrorCode;
import java.text.ParseException;
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
class CoinServiceImplTest {

    private final Integer uid = 1;
    private final Member member = createMember(uid);

    @Mock
    private MemberService memberService;

    @Mock
    private CoinRepository coinRepository;

    @Mock
    private AssetSummaryRepository assetSummaryRepository;

    @InjectMocks
    private CoinServiceImpl coinService;

    @Test
    @Transactional
    void shouldReturnCoinList_whenCoinsExist() {
        // Given
        List<Coin> coins = List.of(new Coin());

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(coinRepository.findByUidAndDeleteDateIsNull(member)).thenReturn(coins);

        // When
        List<CoinDTO> coinList = coinService.getCoinList(uid);

        // Then
        assertThat(coinList).isNotEmpty();
        verify(memberService).findMemberByUid(uid);
        verify(coinRepository).findByUidAndDeleteDateIsNull(member);
    }


    @Test
    @Transactional
    void shouldThrowException_whenNoCoinsExist() {
        // Given
        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(coinRepository.findByUidAndDeleteDateIsNull(member)).thenReturn(Collections.emptyList());

        // When & Then
        assertThatThrownBy(() -> coinService.getCoinList(uid))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.INVALID_COIN.getMessage());
    }

    @Test
    @Transactional
    void shouldSaveAndReturnCoinDTO_whenAddingCoin() throws ParseException {
        // Given
        CoinDTO coinDTO = createCoinDTO();
        Coin coin = createCoin(member, coinDTO);

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(coinRepository.save(any(Coin.class))).thenReturn(coin);

        // When
        CoinDTO result = coinService.addCoin(uid, coinDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCurrency()).isEqualTo("USD");
        assertThat(result.getBalance()).isEqualTo(1000.0);
        assertThat(result.getAvgBuyPrice()).isEqualTo(500.0);
        verifyDependencies();
        verify(coinRepository).save(any(Coin.class));
    }

    @Test
    @Transactional
    void shouldUpdateCoinBalance_whenUpdatingCoin() {
        // Given
        Coin coin = createCoin(member, 1000.0);
        CoinDTO coinDTO = new CoinDTO();
        coinDTO.setIndex(1);
        coinDTO.setBalance(2000.0);

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(coinRepository.findByIndexAndDeleteDateIsNull(coinDTO.getIndex())).thenReturn(Optional.of(coin));
        when(coinRepository.save(any(Coin.class))).thenReturn(coin);

        // When
        CoinDTO result = coinService.updateCoin(uid, coinDTO);

        // Then
        assertThat(result.getBalance()).isEqualTo(2000.0);
        verifyDependencies();
        verify(coinRepository).save(any(Coin.class));
    }

    @Test
    @Transactional
    void shouldMarkCoinAsDeleted_whenDeletingCoin() {
        // Given
        Coin coin = createCoin(member, 1000.0);
        coin.setIndex(1);

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(coinRepository.findByIndexAndDeleteDateIsNull(coin.getIndex())).thenReturn(Optional.of(coin));
        when(coinRepository.save(any(Coin.class))).thenReturn(coin);

        // When
        CoinDTO result = coinService.deleteCoin(uid, coin.getIndex());

        // Then
        assertThat(result.getDeleteDate()).isNotNull();
        verifyDependencies();
        verify(coinRepository).save(any(Coin.class));
    }

    // Helper Methods
    private Member createMember(Integer uid) {
        Member member = new Member();
        member.setUid(uid);
        return member;
    }

    private Coin createCoin(Member member, CoinDTO coinDTO) {
        Coin coin = new Coin();
        coin.setUid(member);
        coin.setCurrency(coinDTO.getCurrency());
        coin.setBalance(coinDTO.getBalance());
        coin.setAvgBuyPrice(coinDTO.getAvgBuyPrice());
        coin.setUnitCurrency("USD");
        coin.setCategory("Crypto");
        coin.setAddDate(new Date());
        coin.setDeleteDate(null);
        return coin;
    }

    private Coin createCoin(Member member, double balance) {
        Coin coin = new Coin();
        coin.setUid(member);
        coin.setCurrency("USD");
        coin.setBalance(balance);
        coin.setAvgBuyPrice(500.0);
        coin.setUnitCurrency("USD");
        coin.setCategory("Crypto");
        coin.setAddDate(new Date());
        coin.setDeleteDate(null);
        return coin;
    }

    private CoinDTO createCoinDTO() {
        CoinDTO coinDTO = new CoinDTO();
        coinDTO.setCurrency("USD");
        coinDTO.setBalance(1000.0);
        coinDTO.setAvgBuyPrice(500.0);
        return coinDTO;
    }

    private void verifyDependencies() {
        verify(memberService).findMemberByUid(uid);
        verify(assetSummaryRepository).insertOrUpdateAssetSummary(uid);
    }
}
