package com.idle.kb_i_dle_backend.domain.finance.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.idle.kb_i_dle_backend.config.exception.CustomException;
import com.idle.kb_i_dle_backend.domain.finance.dto.*;
import com.idle.kb_i_dle_backend.domain.finance.entity.*;
import com.idle.kb_i_dle_backend.domain.finance.repository.*;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.global.codes.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class FinanceServiceImplTest {

    @Mock
    private BankRepository bankRepository;

    @Mock
    private AssetSummaryRepository assetSummaryRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private StockProductRepository stockProductRepository;

    @Mock
    private SpotRepository spotRepository;

    @InjectMocks
    private FinanceServiceImpl financeService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setUid(1);
    }

    private void mockMemberRetrieval(int uid) {
        when(memberService.findMemberByUid(uid)).thenReturn(member);
    }

    @Test
    void getFinancialAssetsSum_ShouldReturnFinancialSumDTO() {
        // Arrange
        mockMemberRetrieval(1);
        AssetSummary assetSummary = new AssetSummary();
        assetSummary.setTotalAmount(50000L);
        when(assetSummaryRepository.findLatestByUidZeroMonthAgo(member)).thenReturn(assetSummary);

        // Act
        FinancialSumDTO result = financeService.getFinancialAssetsSum(1);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(50000L);
        verify(assetSummaryRepository).findLatestByUidZeroMonthAgo(member);
    }

    @Test
    void getTotalAssetsSum_ShouldReturnFinancialSumDTO() {
        // Arrange
        mockMemberRetrieval(1);
        AssetSummary assetSummary = new AssetSummary();
        assetSummary.setTotalAmount(50000L);
        when(assetSummaryRepository.findLatestByUidZeroMonthAgo(member)).thenReturn(assetSummary);

        // Spot 객체를 생성하고 필드 값을 설정합니다.
        Spot spot = new Spot();
        spot.setIndex(1);
        spot.setUid(member);
        spot.setCategory("자동차");
        spot.setName("Tesla Model S");
        spot.setPrice(20000L); // Spot 자산의 가격을 20000L로 설정하여 기대값을 충족시킵니다.
        spot.setAddDate(new Date());
        spot.setDeleteDate(null);  // 삭제되지 않은 경우 null로 설정

        // List.of()로 Spot 객체를 포함한 리스트 생성
        List<Spot> spots = List.of(spot);

        when(spotRepository.findByUidAndDeleteDateIsNull(member)).thenReturn(spots);

        // Act
        FinancialSumDTO result = financeService.getTotalAssetsSum(1);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(70000L); // 50000 + 20000
        verify(assetSummaryRepository).findLatestByUidZeroMonthAgo(member);
    }


    @Test
    void getTotalAssetsSum_ShouldThrowException_WhenAssetSummaryNotFound() {
        // Arrange
        mockMemberRetrieval(1);
        when(assetSummaryRepository.findLatestByUidZeroMonthAgo(member)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> financeService.getTotalAssetsSum(1))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("Asset summary not found for member: " + 1);
    }

    @Test
    void getFinancialAsset_ShouldReturnAssetDTOList() {
        // Arrange
        mockMemberRetrieval(1);
        AssetSummary assetSummary = new AssetSummary();
        assetSummary.setDeposit(20000L);
        assetSummary.setStock(15000L);
        when(assetSummaryRepository.findLatestByUidZeroMonthAgo(member)).thenReturn(assetSummary);

        // Act
        List<AssetDTO> assetList = financeService.getFinancialAsset(1);

        // Assert
        assertThat(assetList).isNotEmpty();
        assertThat(assetList).anyMatch(asset -> asset.getProdCategory().equals("예적금") && asset.getAmount() == 20000L);
        assertThat(assetList).anyMatch(asset -> asset.getProdCategory().equals("주식") && asset.getAmount() == 15000L);
    }

    @Test
    void getSixMonthFinancialChanges_ShouldReturnSixMonthsData() {
        // Arrange
        mockMemberRetrieval(1);
        Date now = new Date();
        AssetSummary summaryOneMonth = new AssetSummary();
        summaryOneMonth.setTotalAmount(55000L);
        AssetSummary summaryTwoMonths = new AssetSummary();
        summaryTwoMonths.setTotalAmount(53000L);

        when(assetSummaryRepository.findLatestByUidZeroMonthAgo(member)).thenReturn(summaryOneMonth);
        when(assetSummaryRepository.findLatestByUidOneMonthAgo(eq(member), any(Date.class)))
                .thenReturn(summaryOneMonth, summaryTwoMonths);

        // Act
        List<FinancialChangeDTO> changes = financeService.getSixMonthFinancialChanges(1);

        // Assert
        assertThat(changes).hasSize(6);
        assertThat(changes.get(0).getBalance()).isEqualTo(55000L);
        assertThat(changes.get(1).getBalance()).isEqualTo(55000L);
        assertThat(changes.get(2).getBalance()).isEqualTo(53000L);
    }

    @Test
    void getStockReturnTrend_ShouldReturnStockReturnTrend() {
        // Arrange
        mockMemberRetrieval(1);

        // Stock 객체 설정
        Stock stock = new Stock();
        stock.setAvgBuyPrice(1000);  // 구매 가격 설정
        stock.setPdno(1); // 필요 시 설정

        // StockProduct 객체 설정
        StockProduct stockProduct = new StockProduct();
        stockProduct.setPrice(1100);  // 현재 가격 설정
        StockProductPrice stockProductPrice = new StockProductPrice();
        stockProductPrice.setOneMonthAgoPrice(1050); // 1개월 전 가격 설정
        stockProduct.setStockProductPrice(stockProductPrice);

        List<Stock> stocks = List.of(stock);

        when(stockRepository.findAllByUidAndDeleteDateIsNull(member)).thenReturn(stocks);
        when(stockProductRepository.findByShortCode(anyString())).thenReturn(stockProduct);

        // Act
        List<StockReturnDTO> stockReturnTrend = financeService.getStockReturnTrend(1);

        // Assert
        assertThat(stockReturnTrend).hasSize(6);
        assertThat(stockReturnTrend.get(0).getEarningRate()).isEqualTo(10.0); // 현재 가격이 1100, 구매 가격이 1000일 때 수익률은 10%
    }
}
