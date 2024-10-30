package com.idle.kb_i_dle_backend.domain.invest.service.impl;

import com.idle.kb_i_dle_backend.domain.finance.entity.Bank;
import com.idle.kb_i_dle_backend.domain.finance.entity.Bond;
import com.idle.kb_i_dle_backend.domain.finance.entity.BondProduct;
import com.idle.kb_i_dle_backend.domain.finance.entity.Coin;
import com.idle.kb_i_dle_backend.domain.finance.entity.CoinProduct;
import com.idle.kb_i_dle_backend.domain.finance.entity.Stock;
import com.idle.kb_i_dle_backend.domain.finance.entity.StockPrice;
import com.idle.kb_i_dle_backend.domain.finance.repository.*;
import com.idle.kb_i_dle_backend.domain.invest.dto.AvailableCashDTO;
import com.idle.kb_i_dle_backend.domain.invest.dto.CategorySumDTO;
import com.idle.kb_i_dle_backend.domain.invest.dto.HighReturnProductDTO;
import com.idle.kb_i_dle_backend.domain.invest.dto.HighReturnProductsDTO;
import com.idle.kb_i_dle_backend.domain.invest.dto.InvestDTO;
import com.idle.kb_i_dle_backend.domain.invest.dto.MaxPercentageCategoryDTO;
import com.idle.kb_i_dle_backend.domain.invest.dto.RecommendedProductDTO;
import com.idle.kb_i_dle_backend.domain.invest.service.InvestService;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.repository.MemberRepository;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvestServiceImpl implements InvestService {
    private final BankRepository bankRepository;
    private final BondRepository bondRepository;
    private final CoinRepository coinRepository;
    private final StockRepository stockRepository;
    private final BondProductRepository bondProductRepository;
    private final StockPriceRepository stockPriceRepository;
    private final CoinPriceRepository coinPriceRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;


    @Override
    public List<InvestDTO> getInvestList(int uid) throws Exception {
        Member member = memberService.findMemberByUid(uid);
        List<Bank> banks = bankRepository.findByUid(member);
        List<Bond> bonds = bondRepository.findByUid(member);
        List<Coin> coins = coinRepository.findByUid(member);
        List<Stock> stocks = stockRepository.findByUid(member);

        List<InvestDTO> investDTOs = new ArrayList<>();
        investDTOs.addAll(InvestDTO.fromUserBankList(banks));
        investDTOs.addAll(InvestDTO.fromBondList(bonds));
        investDTOs.addAll(InvestDTO.fromCoinList(coins));
        investDTOs.addAll(InvestDTO.fromStockList(stocks));

        return investDTOs;
    }

    @Override
    public long totalAsset(int uid) throws Exception {
        Member member = memberService.findMemberByUid(uid);
        List<InvestDTO> investDTOs = getInvestList(uid);

        return investDTOs.stream()
                .mapToLong(InvestDTO::getPrice)
                .sum();
    }

    @Override
    public MaxPercentageCategoryDTO getMaxPercentageCategory(int uid) throws Exception {
        Member member = memberRepository.findByUid(uid);
        List<CategorySumDTO> categorySums = getInvestmentTendency(uid);

        return categorySums.stream()
                .max(Comparator.comparingDouble(CategorySumDTO::getPercentage))
                .map(category -> new MaxPercentageCategoryDTO(
                        category.getCategory(),
                        category.getTotalPrice(),
                        category.getPercentage()))
                .orElseThrow(() -> new Exception("No categories found"));
    }

    @Override
    public AvailableCashDTO getAvailableCash(int uid) throws Exception {
        Member member = memberService.findMemberByUid(uid);
        List<Bank> banks = bankRepository.findByUidAndSpecificCategoriesAndDeleteDateIsNull(member);

        Long totalAvailableCash = banks.stream()
                .mapToLong(Bank::getBalanceAmt)
                .sum();

        Long totalAsset = totalAsset(uid);

        return new AvailableCashDTO(totalAvailableCash, totalAsset);
    }

    @Override
    public List<CategorySumDTO> getInvestmentTendency(int uid) throws Exception {
        Member member = memberService.findMemberByUid(uid);
        List<InvestDTO> investDTOs = getInvestList(uid);

        Map<String, Long> categorySums = investDTOs.stream()
                .collect(Collectors.groupingBy(InvestDTO::getCategory,
                        Collectors.summingLong(InvestDTO::getPrice)));

        long totalInvestment = totalAsset(uid);

        return categorySums.entrySet().stream()
                .map(entry -> {
                    String category = entry.getKey();
                    Long totalPrice = entry.getValue();
                    Double percentage = (totalPrice.doubleValue() / totalInvestment) * 100;
                    return new CategorySumDTO(category, totalPrice, percentage);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<RecommendedProductDTO> getRecommendedProducts(int uid) throws Exception {
        MaxPercentageCategoryDTO maxCategory = getMaxPercentageCategory(uid);
        List<RecommendedProductDTO> recommendedProducts = new ArrayList<>();

        if ("안정형".equals(maxCategory.getTendency())) {
            List<CoinProduct> coins = coinRepository.findTop5ByOrderByClosingPriceDesc()
                    .stream().limit(5).collect(Collectors.toList());
            List<StockPrice> stocks = stockPriceRepository.findTop5ByLatestDateOrderByPriceDesc();

            recommendedProducts.addAll(coins.stream()
                    .map(coin -> new RecommendedProductDTO("코인", coin.getCoinName(),
                            (int) Double.parseDouble(coin.getClosingPrice())))
                    .collect(Collectors.toList()));

            recommendedProducts.addAll(stocks.stream()
                    .map(stock -> new RecommendedProductDTO("주식", stock.getStock_nm(), stock.getPrice()))
                    .collect(Collectors.toList()));
        } else {
            List<BondProduct> bonds = bondProductRepository.findTop5ByOrderByPriceDesc();

            recommendedProducts.addAll(bonds.stream()
                    .map(bond -> new RecommendedProductDTO("채권", bond.getIsinCdNm(), bond.getPrice()))
                    .collect(Collectors.toList()));
        }

        return recommendedProducts;
    }

    @Override
    public List<HighReturnProductDTO> getHighReturnStock(int uid) throws Exception {
        List<Object[]> highReturnStocks = stockPriceRepository.findPriceDifferenceBetweenLastTwoDates();
        return highReturnStocks.stream()
                .filter(stock -> stock.length >= 5 && stock[2] != null && stock[3] != null && stock[4] != null)
                .map(stock -> {
                    String standardCode = (String) stock[0];
                    String stockName = (String) stock[1];
                    int priceDifference = ((Number) stock[2]).intValue();
                    int previousPrice = ((Number) stock[3]).intValue();
                    int latestPrice = ((Number) stock[4]).intValue();

                    double rate = previousPrice != 0 ? (double) priceDifference / previousPrice * 100 : 0;
                    String formattedRate = String.format("%.2f%%", rate);

                    return new HighReturnProductDTO(
                            "주식",
                            stockName,
                            latestPrice,
                            formattedRate
                    );
                })
                .limit(5)
                .collect(Collectors.toList());
    }

    @Override
    public List<HighReturnProductDTO> getHighReturnCoin(int uid) throws Exception {
        List<Object[]> highReturnCoins = coinPriceRepository.findPriceDifferenceBetweenLastTwoDates();
        return highReturnCoins.stream()
                .filter(coin -> coin.length >= 4 && coin[1] != null && coin[2] != null && coin[3] != null)
                .map(coin -> {
                    String coinName = (String) coin[0];
                    double priceDifference = ((Number) coin[1]).doubleValue();
                    double previousPrice = ((Number) coin[2]).doubleValue();
                    double latestPrice = ((Number) coin[3]).doubleValue();

                    double rate = previousPrice != 0 ? (priceDifference / previousPrice) * 100 : 0;
                    String formattedRate = String.format("%.2f%%", rate);

                    return new HighReturnProductDTO(
                            "코인",
                            coinName,
                            (int) latestPrice,
                            formattedRate
                    );
                })
                .limit(5)
                .collect(Collectors.toList());
    }

    @Override
    public HighReturnProductsDTO getHighReturnProducts(int uid) {
        CompletableFuture<List<HighReturnProductDTO>> stocksFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return getHighReturnStock(uid);
            } catch (Exception e) {
                log.error("Error getting high return stocks", e);
                return List.of();
            }
        });

        CompletableFuture<List<HighReturnProductDTO>> coinsFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return getHighReturnCoin(uid);
            } catch (Exception e) {
                log.error("Error getting high return coins", e);
                return List.of();
            }
        });

        try {
            CompletableFuture.allOf(stocksFuture, coinsFuture).join();

            List<HighReturnProductDTO> stocks = stocksFuture.get();
            List<HighReturnProductDTO> coins = coinsFuture.get();

            List<HighReturnProductDTO> allProducts = new ArrayList<>(stocks);
            allProducts.addAll(coins);
            allProducts.sort(Comparator.comparing(HighReturnProductDTO::getRate).reversed());

            return new HighReturnProductsDTO(allProducts);
        } catch (Exception e) {
            log.error("Error getting high return products", e);
            return new HighReturnProductsDTO(List.of());
        }
    }
}