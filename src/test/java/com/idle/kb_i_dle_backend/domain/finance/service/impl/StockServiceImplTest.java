package com.idle.kb_i_dle_backend.domain.finance.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.idle.kb_i_dle_backend.config.exception.CustomException;
import com.idle.kb_i_dle_backend.domain.finance.dto.StockDTO;
import com.idle.kb_i_dle_backend.domain.finance.entity.Stock;
import com.idle.kb_i_dle_backend.domain.finance.repository.AssetSummaryRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.StockRepository;
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
class StockServiceImplTest {

    private final Integer uid = 1;
    private final Member member = createMember(uid);

    @Mock
    private MemberService memberService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private AssetSummaryRepository assetSummaryRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    @Transactional
    void shouldReturnStockList_whenStocksExist() {
        // Given
        List<Stock> stocks = List.of(new Stock());

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(stockRepository.findByUidAndDeleteDateIsNull(member)).thenReturn(stocks);

        // When
        List<StockDTO> stockList = stockService.getStockList(uid);

        // Then
        assertThat(stockList).isNotEmpty();
        verify(memberService).findMemberByUid(uid);
        verify(stockRepository).findByUidAndDeleteDateIsNull(member);
    }

    @Test
    @Transactional
    void shouldThrowException_whenNoStocksExist() {
        // Given
        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(stockRepository.findByUidAndDeleteDateIsNull(member)).thenReturn(Collections.emptyList());

        // When & Then
        assertThatThrownBy(() -> stockService.getStockList(uid))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("user dont have stocks");

    }

    @Test
    @Transactional
    void shouldSaveAndReturnStockDTO_whenAddingStock() throws ParseException {
        // Given
        StockDTO stockDTO = createStockDTO();
        Stock stock = createStock(member, stockDTO);

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);

        // When
        StockDTO result = stockService.addStock(uid, stockDTO);

        // Then
        assertThat(result).isNotNull();
        verifyDependencies();
        verify(stockRepository).save(any(Stock.class));
    }

    @Test
    @Transactional
    void shouldUpdateStockQuantity_whenUpdatingStock() {
        // Given
        Stock stock = createStock(member, 100);
        StockDTO stockDTO = new StockDTO();
        stockDTO.setIndex(1);
        stockDTO.setHldgQty(200);

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(stockRepository.findByIndexAndDeleteDateIsNull(stockDTO.getIndex())).thenReturn(Optional.of(stock));
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);

        // When
        StockDTO result = stockService.updateStock(uid, stockDTO);

        // Then
        assertThat(result.getHldgQty()).isEqualTo(200);
        verifyDependencies();
        verify(stockRepository).save(any(Stock.class));
    }

    @Test
    @Transactional
    void shouldMarkStockAsDeleted_whenDeletingStock() {
        // Given
        Stock stock = createStock(member, 100);
        stock.setIndex(1);

        when(memberService.findMemberByUid(uid)).thenReturn(member);
        when(stockRepository.findByIndexAndDeleteDateIsNull(stock.getIndex())).thenReturn(Optional.of(stock));
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);

        // When
        StockDTO result = stockService.deleteStock(uid, stock.getIndex());

        // Then
        assertThat(result.getDeleteDate()).isNotNull();
        verifyDependencies();
        verify(stockRepository).save(any(Stock.class));
    }

    // Helper Methods
    private Member createMember(Integer uid) {
        Member member = new Member();
        member.setUid(uid);
        return member;
    }

    private Stock createStock(Member member, int quantity) {
        Stock stock = new Stock();
        stock.setUid(member);
        stock.setHldgQty(quantity);
        stock.setDeleteDate(null);
        return stock;
    }

    private Stock createStock(Member member, StockDTO stockDTO) {
        Stock stock = new Stock();
        stock.setUid(member);
        stock.setHldgQty(stockDTO.getHldgQty());
        stock.setDeleteDate(null);
        return stock;
    }

    private StockDTO createStockDTO() {
        StockDTO stockDTO = new StockDTO();
        stockDTO.setHldgQty(100);
        return stockDTO;
    }

    private void verifyDependencies() {
        verify(memberService).findMemberByUid(uid);
        verify(assetSummaryRepository).insertOrUpdateAssetSummary(uid);
    }
}
