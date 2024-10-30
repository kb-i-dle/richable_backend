package com.idle.kb_i_dle_backend.domain.invest.service.impl;

import com.idle.kb_i_dle_backend.config.exception.CustomException;
import com.idle.kb_i_dle_backend.domain.finance.dto.StockDTO;
import com.idle.kb_i_dle_backend.domain.finance.entity.Bank;
import com.idle.kb_i_dle_backend.domain.finance.entity.CoinProduct;
import com.idle.kb_i_dle_backend.domain.finance.entity.StockPrice;
import com.idle.kb_i_dle_backend.domain.finance.repository.BankRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.BondRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.CoinPriceRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.CoinRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.StockPriceRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.StockRepository;
import com.idle.kb_i_dle_backend.domain.invest.dto.AvailableCashDTO;
import com.idle.kb_i_dle_backend.domain.invest.dto.HighReturnProductDTO;
import com.idle.kb_i_dle_backend.domain.invest.dto.HighReturnProductsDTO;
import com.idle.kb_i_dle_backend.domain.invest.dto.InvestDTO;
import com.idle.kb_i_dle_backend.domain.invest.dto.MaxPercentageCategoryDTO;
import com.idle.kb_i_dle_backend.domain.invest.dto.RecommendedProductDTO;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.repository.MemberRepository;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.domain.invest.dto.CategorySumDTO;
import com.idle.kb_i_dle_backend.global.codes.ErrorCode;
import java.util.Collections;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.offset;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    private CoinRepository coinRepository;
    @Mock
    private StockRepository stockRepository;
    @Mock
    private BondRepository bondRepository;
    @Mock
    private CoinPriceRepository coinPriceRepository;
    @Mock
    private MemberService memberService;

    @InjectMocks
    private InvestServiceImpl investService;

    private Member mockMember;

    @BeforeEach
    void setUp() {
        mockMember = new Member();
        mockMember.setUid(1);

        lenient().when(memberService.findMemberByUid(anyInt())).thenReturn(mockMember);
        lenient().when(memberRepository.findByUid(anyInt())).thenReturn(mockMember);
    }


    @Test
    void testGetAvailableCash_Success() {
        // Given: Mock 설정
        Bank mockBank = new Bank();
        mockBank.setBalanceAmt(5000L);
        when(bankRepository.findByUidAndSpecificCategoriesAndDeleteDateIsNull(mockMember))
                .thenReturn(List.of(mockBank));

        // When: 메서드 호출
        AvailableCashDTO result = investService.getAvailableCash(1);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getAvailableCash()).isEqualTo(5000L);
    }

    // 수정된 테스트 코드 예시
    @Test
    void testGetHighReturnStock_Success() {
        // Given: Mock 설정
        HighReturnProductDTO expectedStock = new HighReturnProductDTO("주식", "AAPL", 150, "50.00%");
        when(stockPriceRepository.findPriceDifferenceBetweenLastTwoDates())
                .thenReturn(Collections.singletonList(new Object[]{"AAPL", "AAPL", 50, 100, 150}));

        // When: 메서드 호출
        List<HighReturnProductDTO> result = investService.getHighReturnStock(1);

        // Then: 결과 검증
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getCategory()).isEqualTo(expectedStock.getCategory());
        assertThat(result.get(0).getName()).isEqualTo(expectedStock.getName());
        assertThat(result.get(0).getPrice()).isEqualTo(expectedStock.getPrice());
        assertThat(result.get(0).getRate()).isEqualTo(expectedStock.getRate());
    }


    @Test
    void testGetHighReturnCoin_Success() {
        // Given: Mock 설정
        HighReturnProductDTO expectedCoin = new HighReturnProductDTO("코인", "Bitcoin", 40000, "11.11%");
        when(coinPriceRepository.findPriceDifferenceBetweenLastTwoDates())
                .thenReturn(Collections.singletonList(new Object[]{"Bitcoin", 4000.0, 36000.0, 40000.0}));

        // When: 메서드 호출
        List<HighReturnProductDTO> result = investService.getHighReturnCoin(1);

        // Then: 결과 검증
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getCategory()).isEqualTo(expectedCoin.getCategory());
        assertThat(result.get(0).getName()).isEqualTo(expectedCoin.getName());
        assertThat(result.get(0).getPrice()).isEqualTo(expectedCoin.getPrice());
        assertThat(result.get(0).getRate()).isEqualTo(expectedCoin.getRate());
    }



    @Test
    void testGetHighReturnProducts_Success() {
        // Given: Mock 설정
        HighReturnProductDTO expectedStock = new HighReturnProductDTO("주식", "AAPL", 150, "50.00%");
        HighReturnProductDTO expectedCoin = new HighReturnProductDTO("코인", "Bitcoin", 40000, "11.11%");

        when(stockPriceRepository.findPriceDifferenceBetweenLastTwoDates())
                .thenReturn(Collections.singletonList(new Object[]{"AAPL","AAPL", 50, 100, 150})); // 수익률 계산에 맞게 수정
        when(coinPriceRepository.findPriceDifferenceBetweenLastTwoDates())
                .thenReturn(Collections.singletonList(new Object[]{"Bitcoin", 4000.0, 36000.0, 40000.0}));

        // When: 메서드 호출
        HighReturnProductsDTO result = investService.getHighReturnProducts(1);

        // Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getProducts()).hasSize(2);
        assertThat(result.getProducts().get(0).getName()).isEqualTo(expectedStock.getName());
        assertThat(result.getProducts().get(0).getRate()).isEqualTo(expectedStock.getRate());
        assertThat(result.getProducts().get(1).getName()).isEqualTo(expectedCoin.getName());
        assertThat(result.getProducts().get(1).getRate()).isEqualTo(expectedCoin.getRate());
    }

}