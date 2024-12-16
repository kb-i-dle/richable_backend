package com.idle.kb_i_dle_backend.domain.invest.service.impl;

import com.idle.kb_i_dle_backend.domain.finance.entity.Bank;
import com.idle.kb_i_dle_backend.domain.finance.entity.BondProduct;
import com.idle.kb_i_dle_backend.domain.finance.entity.CoinProduct;
import com.idle.kb_i_dle_backend.domain.finance.entity.StockPrice;
import com.idle.kb_i_dle_backend.domain.finance.repository.BankRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.BondProductRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.BondRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.CoinPriceRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.CoinRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.StockPriceRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.StockRepository;
import com.idle.kb_i_dle_backend.domain.invest.dto.AvailableCashDTO;
import com.idle.kb_i_dle_backend.domain.invest.dto.CategorySumDTO;
import com.idle.kb_i_dle_backend.domain.invest.dto.HighReturnProductDTO;
import com.idle.kb_i_dle_backend.domain.invest.dto.HighReturnProductsDTO;
import com.idle.kb_i_dle_backend.domain.invest.dto.InvestDTO;
import com.idle.kb_i_dle_backend.domain.invest.dto.MaxPercentageCategoryDTO;
import com.idle.kb_i_dle_backend.domain.invest.dto.RecommendedProductDTO;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.repository.MemberRepository;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import java.util.Collections;
import java.util.Date;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;
import org.mockito.MockitoAnnotations;

@Transactional
@ExtendWith(MockitoExtension.class)
class InvestServiceImplTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private BankRepository bankRepository;
    @Mock
    private StockPriceRepository stockPriceRepository;
    @Mock
    private CoinPriceRepository coinPriceRepository;
    @Mock
    private BondRepository bondRepository;
    @Mock
    private StockRepository stockRepository;
    @Mock
    private CoinRepository coinRepository;
    @Mock
    private BondProductRepository bondProductRepository;
    @Mock
    private MemberService memberService;

    private InvestServiceImpl investService;

    private Member mockMember;

    @BeforeEach
    void setUp() {
        // Mock 객체 초기화
        MockitoAnnotations.openMocks(this);

        // Mock Member 생성
        mockMember = new Member();
        mockMember.setUid(1);

        lenient().when(memberService.findMemberByUid(anyInt())).thenReturn(mockMember);
        lenient().when(memberRepository.findByUid(anyInt())).thenReturn(mockMember);

        // InvestServiceImpl 초기화
        investService = spy(new InvestServiceImpl(
                bankRepository,
                bondRepository,
                coinRepository,
                stockRepository,
                bondProductRepository,
                stockPriceRepository,
                coinPriceRepository,
                memberService,
                memberRepository
        ));
    }

    @Test
    void testGetMaxPercentageCategory_Success() throws Exception {
        // Given
        int uid = 1;
        Member mockMember = new Member();
        mockMember.setUid(uid);

        // CategorySumDTO 목 데이터 설정
        CategorySumDTO mockCategory1 = new CategorySumDTO("주식", 10000L, 60.0);
        CategorySumDTO mockCategory2 = new CategorySumDTO("채권", 5000L, 40.0);

        // 목 메서드 설정
        when(memberRepository.findByUid(uid)).thenReturn(mockMember);
        doReturn(List.of(mockCategory1, mockCategory2))
                .when(investService).getInvestmentTendency(uid);

        // When
        MaxPercentageCategoryDTO result = investService.getMaxPercentageCategory(uid);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCategory()).isEqualTo("주식");
        assertThat(result.getTotalPrice()).isEqualTo(10000L);
        assertThat(result.getPercentage()).isEqualTo(60.0);
    }


    @Test
    void testGetAvailableCash_Success() throws Exception {
        // Given
        Bank mockBank = new Bank();
        mockBank.setBalanceAmt(5000L);

        when(bankRepository.findByUidAndSpecificCategoriesAndDeleteDateIsNull(mockMember))
                .thenReturn(List.of(mockBank));

        List<InvestDTO> mockInvestDTOs = List.of(new InvestDTO(1, "Bank", "KB Bank", 5000L));
        doReturn(mockInvestDTOs).when(investService).getInvestList(1);

        // When
        AvailableCashDTO result = investService.getAvailableCash(1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAvailableCash()).isEqualTo(5000L);
    }

    @Test
    void testGetRecommendedProducts_Tendency_Stable() {
        // Given
        int uid = 1;

        doReturn(new MaxPercentageCategoryDTO("안정형", 10000L, 60.0))
                .when(investService).getMaxPercentageCategory(uid);

        // Mock 데이터 설정
        CoinProduct mockCoin = createMockCoin("Bitcoin", "50000.0");
        StockPrice mockStock = createMockStock("Apple Inc.", 150);

        when(coinRepository.findTop5ByOrderByClosingPriceDesc()).thenReturn(List.of(mockCoin));
        when(stockPriceRepository.findTop5ByLatestDateOrderByPriceDesc()).thenReturn(List.of(mockStock));

        // When
        List<RecommendedProductDTO> result = investService.getRecommendedProducts(uid);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2); // 코인 1개, 주식 1개

        assertThat(result.get(0).getName()).isEqualTo("Bitcoin");
        assertThat(result.get(0).getPrice()).isEqualTo(50000);

        assertThat(result.get(1).getName()).isEqualTo("Apple Inc.");
        assertThat(result.get(1).getPrice()).isEqualTo(150);
    }

    @Test
    void testGetHighReturnProducts_Success() {
        // Given: Mock 데이터 설정
        when(stockPriceRepository.findPriceDifferenceBetweenLastTwoDates())
                .thenReturn(List.of(
                        new Object[]{"AAPL", "AAPL", 50, 100, 150},  // 50% 수익률
                        new Object[]{"MSFT", "MSFT", 30, 70, 100}    // 42.86% 수익률
                ));

        when(coinPriceRepository.findPriceDifferenceBetweenLastTwoDates())
                .thenReturn(List.of(
                        new Object[]{"Bitcoin", 4000.0, 36000.0, 40000.0},  // 11.11% 수익률
                        new Object[]{"Ethereum", 100.0, 900.0, 1000.0}    // 11.11% 수익률
                ));

        // When: 비동기 작업이 완료된 뒤 호출
        HighReturnProductsDTO result = investService.getHighReturnProducts(1);

        // Then: 반환값 검증
        assertThat(result).isNotNull();
        assertThat(result.getProducts()).hasSize(4);

        // 검증: 수익률이 높은 순서대로 정렬되었는지 확인
        assertThat(result.getProducts().get(0).getName()).isEqualTo("AAPL"); // 50% 수익률
        assertThat(result.getProducts().get(0).getRate()).isEqualTo("50.00%");

        assertThat(result.getProducts().get(1).getName()).isEqualTo("MSFT"); // 42.86% 수익률
        assertThat(result.getProducts().get(1).getRate()).isEqualTo("42.86%");

        assertThat(result.getProducts().get(2).getName()).isEqualTo("Bitcoin"); // 11.11% 수익률
        assertThat(result.getProducts().get(2).getRate()).isEqualTo("11.11%");

        assertThat(result.getProducts().get(3).getName()).isEqualTo("Ethereum"); // 11.11% 수익률
        assertThat(result.getProducts().get(3).getRate()).isEqualTo("11.11%");
    }

    @Test
    void testGetHighReturnStock_Success() {
        HighReturnProductDTO expectedStock = new HighReturnProductDTO("주식", "AAPL", 150, "50.00%");

        when(stockPriceRepository.findPriceDifferenceBetweenLastTwoDates())
                .thenReturn(Collections.singletonList(new Object[]{"AAPL", "AAPL", 50, 100, 150}));

        List<HighReturnProductDTO> result = investService.getHighReturnStock(1);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getCategory()).isEqualTo(expectedStock.getCategory());
        assertThat(result.get(0).getName()).isEqualTo(expectedStock.getName());
        assertThat(result.get(0).getPrice()).isEqualTo(expectedStock.getPrice());
        assertThat(result.get(0).getRate()).isEqualTo(expectedStock.getRate());
    }

    @Test
    void testGetHighReturnCoin_Success() {
        HighReturnProductDTO expectedCoin = new HighReturnProductDTO("코인", "Bitcoin", 40000, "11.11%");

        when(coinPriceRepository.findPriceDifferenceBetweenLastTwoDates())
                .thenReturn(Collections.singletonList(new Object[]{"Bitcoin", 4000.0, 36000.0, 40000.0}));

        List<HighReturnProductDTO> result = investService.getHighReturnCoin(1);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getCategory()).isEqualTo(expectedCoin.getCategory());
        assertThat(result.get(0).getName()).isEqualTo(expectedCoin.getName());
        assertThat(result.get(0).getPrice()).isEqualTo(expectedCoin.getPrice());
        assertThat(result.get(0).getRate()).isEqualTo(expectedCoin.getRate());
    }

    private CoinProduct createMockCoin(String name, String closingPrice) {
        CoinProduct coin = new CoinProduct();
        coin.setId(1);
        coin.setCoinName(name);
        coin.setClosingPrice(closingPrice);
        coin.setUpdateAt(new Date());
        return coin;
    }

    private BondProduct createMockBond(String name, int price) {
        BondProduct bond = new BondProduct();
        bond.setId(1L);
        bond.setIsinCdNm(name);
        bond.setPrice(price);
        return bond;
    }

    private StockPrice createMockStock(String name, int price) {
        StockPrice stock = new StockPrice();
        stock.setStandard_code("STK12345");
        stock.setStock_nm(name);
        stock.setPrice(price);
        stock.setDate(new Date());
        return stock;
    }
}
