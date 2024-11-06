package com.idle.kb_i_dle_backend.domain.finance.service.impl;

import com.idle.kb_i_dle_backend.config.exception.CustomException;
import com.idle.kb_i_dle_backend.domain.finance.dto.AssetDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.BondReturnDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.CoinReturnDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.FinancialChangeDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.FinancialSumDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.MonthlyBalanceDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.StockReturnDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.TotalChangeDTO;
import com.idle.kb_i_dle_backend.domain.finance.entity.AssetSummary;
import com.idle.kb_i_dle_backend.domain.finance.entity.Bond;
import com.idle.kb_i_dle_backend.domain.finance.entity.BondProduct;
import com.idle.kb_i_dle_backend.domain.finance.entity.BondProductPrice;
import com.idle.kb_i_dle_backend.domain.finance.entity.Coin;
import com.idle.kb_i_dle_backend.domain.finance.entity.CoinProduct;
import com.idle.kb_i_dle_backend.domain.finance.entity.CoinProductPrice;
import com.idle.kb_i_dle_backend.domain.finance.entity.Spot;
import com.idle.kb_i_dle_backend.domain.finance.entity.Stock;
import com.idle.kb_i_dle_backend.domain.finance.entity.StockProduct;
import com.idle.kb_i_dle_backend.domain.finance.entity.StockProductPrice;
import com.idle.kb_i_dle_backend.domain.finance.repository.AssetSummaryRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.BankRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.BondProductPriceRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.BondProductRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.BondRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.CoinProductPriceRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.CoinRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.SpotRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.StockProductPriceRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.StockPriceRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.StockProductRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.StockRepository;
import com.idle.kb_i_dle_backend.domain.finance.service.FinanceService;
import com.idle.kb_i_dle_backend.domain.income.repository.IncomeRepository;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.repository.MemberRepository;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.domain.outcome.repository.OutcomeUserRepository;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.idle.kb_i_dle_backend.global.codes.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FinanceServiceImpl implements FinanceService {

    private final BankRepository bankRepository;
    private final BondRepository bondRepository;
    private final BondProductRepository bondProductRepository;
    private final BondProductPriceRepository bondProductPriceRepository;
    private final CoinRepository coinRepository;
    private final CoinProductPriceRepository coinProductPriceRepository;
    private final SpotRepository spotRepository;
    private final StockPriceRepository stockPriceRepository;
    private final StockProductRepository stockProductRepository;
    private final StockProductPriceRepository stockProductPriceRepository;
    private final StockRepository stockRepository;
    private final IncomeRepository incomeRepository;
    private final OutcomeUserRepository outComeUserRepository;
    private final AssetSummaryRepository assetSummaryRepository;
    private final MemberRepository memberRepository;

    private final MemberService memberService;



    // 소수점 이하 한 자리로 포맷팅할 수 있는 DecimalFormat
    private static final DecimalFormat df = new DecimalFormat("#.#");

    // 금융 자산 합계 계산
    @Override
    public FinancialSumDTO getFinancialAssetsSum(int uid) {
        try {
            Member member = memberService.findMemberByUid(uid);
            Long financialAssetsSum = Optional.ofNullable(assetSummaryRepository.findLatestByUidZeroMonthAgo(member))
                    .map(AssetSummary::getTotalAmount)
                    .orElse(0L); // 데이터가 없을 경우 0을 기본값으로 설정
            return new FinancialSumDTO(financialAssetsSum);
        } catch (DataAccessException e) {
            // 데이터베이스 관련 예외 처리
            throw new CustomException(ErrorCode.DATABASE_ERROR, "Failed to retrieve financial asset summary");
        } catch (Exception e) {
            // 기타 예외 처리
            throw new CustomException(ErrorCode.UNKNOWN_ERROR, "An unexpected error occurred");
        }
    }


    // 총 자산 합계 계산 (금융 자산 + Spot 자산)
    @Override
    public FinancialSumDTO getTotalAssetsSum(int uid) {
        Member member = memberService.findMemberByUid(uid);

        // Asset Summary 조회
        Long totalAssetsSum = Optional.ofNullable(assetSummaryRepository.findLatestByUidZeroMonthAgo(member))
                .map(AssetSummary::getTotalAmount)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ERROR, "Asset summary not found for member: " + uid));

        // Spot Asset 계산
        totalAssetsSum += calculateSpotAssetsSum(member);

        return new FinancialSumDTO(totalAssetsSum);
    }

    @Override
    public FinancialSumDTO getAssetSummeryByDateBefore(int uid, Date date) {
        Member member = memberService.findMemberByUid(uid);
        AssetSummary assetSummary = assetSummaryRepository.findFirstByUidAndUpdateDateBeforeOrderByUpdateDateDesc(
                member, date);

        if (assetSummary == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_ERROR, "No asset summary found before the specified date for UID: " + uid);
        }

        return new FinancialSumDTO(assetSummary.getTotalAmount());
    }

    // 금융 자산 목록 조회
    @Override
    public List<AssetDTO> getFinancialAsset(int uid) {
        Member member = memberService.findMemberByUid(uid);
        AssetSummary assetSummary = assetSummaryRepository.findLatestByUidZeroMonthAgo(member);

        if (assetSummary == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_ERROR, "No asset summary found before the specified date for UID: " + uid);
        }

        List<AssetDTO> assetList = new ArrayList<>();

        Long sumBankAssets = getOrDefault(assetSummary.getDeposit(), 0L) +
                getOrDefault(assetSummary.getSaving(), 0L) +
                getOrDefault(assetSummary.getSubscription(), 0L) +
                getOrDefault(assetSummary.getWithdrawal(), 0L) +
                getOrDefault(assetSummary.getCash(), 0L);

        assetList.add(new AssetDTO("예적금", sumBankAssets));
        assetList.add(new AssetDTO("주식", getOrDefault(assetSummary.getStock(), 0L)));
        assetList.add(new AssetDTO("코인", getOrDefault(assetSummary.getCoin(), 0L)));
        assetList.add(new AssetDTO("채권", getOrDefault(assetSummary.getBond(), 0L)));

        return assetList;
    }

    private Long getOrDefault(Long value, Long defaultValue) {
        return value != null ? value : defaultValue;
    }

    // 6개월 금융 자산 변동 계산
    @Override
    public List<FinancialChangeDTO> getSixMonthFinancialChanges(int uid) {
        Member member = memberService.findMemberByUid(uid);
        Date now = new Date();
        List<FinancialChangeDTO> financialChanges = new ArrayList<>();

        // 최신 값 처리
        AssetSummary zeroMonthAgo = assetSummaryRepository.findLatestByUidZeroMonthAgo(member);
        Long zeroMonthAgoAmount = zeroMonthAgo != null ? zeroMonthAgo.getTotalAmount() : 0L;
        financialChanges.add(new FinancialChangeDTO(1, zeroMonthAgoAmount));

        // 나머지 월별 처리
        for (int i = 1; i <= 5; i++) {
            AssetSummary summary = assetSummaryRepository.findLatestByUidOneMonthAgo(member, getDateBeforeMonths(now, i));
            Long amount = summary != null ? summary.getTotalAmount() : 0L;
            financialChanges.add(new FinancialChangeDTO(i + 1, amount));
        }
        return financialChanges;
    }

    @Override
    public List<TotalChangeDTO> getSixMonthTotalChanges(int uid) {
        Member member = memberService.findMemberByUid(uid);
        List<TotalChangeDTO> totalChanges = new ArrayList<>();
        Date now = new Date();

        for (int i = 0; i < 6; i++) {
            long monthlySum = switch (i) {
                case 0 -> {
                    AssetSummary zeroMonthAgo = assetSummaryRepository.findLatestByUidZeroMonthAgo(member);
                    long zeroMonthAgoAmount = zeroMonthAgo != null ? zeroMonthAgo.getTotalAmount() : 0L;
                    yield zeroMonthAgoAmount + calculateSpotAssetsSum(member);
                }
                case 1 -> {
                    AssetSummary oneMonthAgo = assetSummaryRepository.findLatestByUidOneMonthAgo(member, getDateBeforeMonths(now, 1));
                    long oneMonthAgoAmount = oneMonthAgo != null ? oneMonthAgo.getTotalAmount() : 0L;
                    yield oneMonthAgoAmount + calculateSpotAssetsSumBefore(member, 1);
                }
                case 2 -> {
                    AssetSummary twoMonthAgo = assetSummaryRepository.findLatestByUidOneMonthAgo(member, getDateBeforeMonths(now, 2));
                    long twoMonthAgoAmount = twoMonthAgo != null ? twoMonthAgo.getTotalAmount() : 0L;
                    yield twoMonthAgoAmount + calculateSpotAssetsSumBefore(member, 2);
                }
                case 3 -> {
                    AssetSummary threeMonthAgo = assetSummaryRepository.findLatestByUidOneMonthAgo(member, getDateBeforeMonths(now, 3));
                    long threeMonthAgoAmount = threeMonthAgo != null ? threeMonthAgo.getTotalAmount() : 0L;
                    yield threeMonthAgoAmount + calculateSpotAssetsSumBefore(member, 3);
                }
                case 4 -> {
                    AssetSummary fourMonthAgo = assetSummaryRepository.findLatestByUidOneMonthAgo(member, getDateBeforeMonths(now, 4));
                    long fourMonthAgoAmount = fourMonthAgo != null ? fourMonthAgo.getTotalAmount() : 0L;
                    yield fourMonthAgoAmount + calculateSpotAssetsSumBefore(member, 4);
                }
                case 5 -> {
                    AssetSummary fiveMonthAgo = assetSummaryRepository.findLatestByUidOneMonthAgo(member, getDateBeforeMonths(now, 5));
                    long fiveMonthAgoAmount = fiveMonthAgo != null ? fiveMonthAgo.getTotalAmount() : 0L;
                    yield fiveMonthAgoAmount + calculateSpotAssetsSumBefore(member, 5);
                }
                default -> throw new IllegalStateException("Unexpected value: " + i);
            };
            totalChanges.add(new TotalChangeDTO(i + 1, monthlySum));
        }

        return totalChanges;
    }

    @Override
    public List<StockReturnDTO> getStockReturnTrend(int uid) {
        Member member = memberService.findMemberByUid(uid);
        List<StockReturnDTO> stockReturns = new ArrayList<>();
        List<Stock> stocks = stockRepository.findAllByUidAndDeleteDateIsNull(member);

        if (stocks.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_STOCK, "user dont have stocks");
        }

        for (int i = 0; i < 6; i++) {
            double totalStockReturn = 0;
            int stockCount = 0;

            for (Stock stock : stocks) {
                StockProduct stockProduct = stockProductRepository.findByShortCode(String.valueOf(stock.getPdno()));

                // stockProduct가 null이면 가격을 0으로 설정
                double currentPrice = 0;
                if (stockProduct != null) {
                    if (i == 0) {
                        // 현재 가격 (StockProduct의 price 사용)
                        currentPrice = stockProduct.getPrice() != null ? stockProduct.getPrice() : 0;
                    } else {
                        // i개월 전 가격
                        currentPrice = getPriceForMonth(stockProduct.getStockProductPrice(), i);
                    }
                }

                double purchasePrice = stock.getAvgBuyPrice();

                if (purchasePrice > 0 && currentPrice > 0) {
                    double stockReturn = ((currentPrice / purchasePrice) * 100) - 100;
                    totalStockReturn += stockReturn;
                    stockCount++;
                }
            }

            if (stockCount > 0) {
                double averageStockReturn = totalStockReturn / stockCount;
                stockReturns.add(new StockReturnDTO(i + 1, Double.parseDouble(df.format(averageStockReturn))));
            } else {
                stockReturns.add(new StockReturnDTO(i + 1, 0));
            }
        }

        return stockReturns;
    }

    private double getPriceForMonth(StockProductPrice stockProductPrice, int monthsAgo) {
        return switch (monthsAgo) {
            case 1 -> stockProductPrice.getOneMonthAgoPrice() != null ? stockProductPrice.getOneMonthAgoPrice() : 0;
            case 2 -> stockProductPrice.getTwoMonthsAgoPrice() != null ? stockProductPrice.getTwoMonthsAgoPrice() : 0;
            case 3 -> stockProductPrice.getThreeMonthsAgoPrice() != null ? stockProductPrice.getThreeMonthsAgoPrice() : 0;
            case 4 -> stockProductPrice.getFourMonthsAgoPrice() != null ? stockProductPrice.getFourMonthsAgoPrice() : 0;
            case 5 -> stockProductPrice.getFiveMonthsAgoPrice() != null ? stockProductPrice.getFiveMonthsAgoPrice() : 0;
            default -> 0;
        };
    }

    //6개월 코인 수익률
    @Override
    public List<CoinReturnDTO> getCoinReturnTrend(int uid) {
        Member member = memberService.findMemberByUid(uid);
        List<CoinReturnDTO> coinReturns = new ArrayList<>();
        List<Coin> coins = coinRepository.findAllByUidAndDeleteDateIsNull(member);

        if (coins.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_COIN, "user dont have coin");
        }

        for (int i = 0; i < 6; i++) {
            double totalCoinReturn = 0;
            int coinCount = 0;
            for (Coin coin : coins) {
                CoinProduct coinProduct = coinRepository.findByCoinName(coin.getCurrency());
                if (coinProduct == null) continue;
                CoinProductPrice coinProductPrice = coinProduct.getCoinProductPrice();
                if (coinProductPrice == null) continue;
                double currentPrice;
                if (i == 0) {
                    // 현재 가격 (CoinProduct의 closingPrice 사용)
                    currentPrice = Double.parseDouble(coinProduct.getClosingPrice());
                } else {
                    // i개월 전 가격
                    currentPrice = getPriceForMonth(coinProductPrice, i);
                }
                double purchasePrice = coin.getAvgBuyPrice();

                if (purchasePrice > 0 && currentPrice > 0) {
                    double coinReturn = ((currentPrice / purchasePrice) * 100) - 100;
                    totalCoinReturn += coinReturn;
                    coinCount++;
                }
            }
            if (coinCount > 0) {
                double averageCoinReturn = totalCoinReturn / coinCount;
                coinReturns.add(new CoinReturnDTO(i + 1, Double.parseDouble(df.format(averageCoinReturn))));
            } else {
                coinReturns.add(new CoinReturnDTO(i + 1, 0.0));
            }
        }
        return coinReturns;
    }

    private double getPriceForMonth(CoinProductPrice coinProductPrice, int monthsAgo) {
        return switch (monthsAgo) {
            case 1 -> Double.parseDouble(coinProductPrice.getOneMonthAgoPrice());
            case 2 -> Double.parseDouble(coinProductPrice.getTwoMonthsAgoPrice());
            case 3 -> Double.parseDouble(coinProductPrice.getThreeMonthsAgoPrice());
            case 4 -> Double.parseDouble(coinProductPrice.getFourMonthsAgoPrice());
            case 5 -> Double.parseDouble(coinProductPrice.getFiveMonthsAgoPrice());
            default -> 0;
        };
    }

    //6개월 채권 수익률
    @Override
    public List<BondReturnDTO> getBondReturnTrend(int uid) {
        Member member = memberService.findMemberByUid(uid);
        List<BondReturnDTO> bondReturns = new ArrayList<>();
        List<Bond> bonds = bondRepository.findAllByUidAndDeleteDateIsNull(member);
        if (bonds.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_BOND, "user dont have bond");
        }

        for (int i = 0; i < 6; i++) {
            double totalBondReturn = 0;
            int bondCount = 0;

            for (Bond bond : bonds) {
                BondProduct bondProduct = bondRepository.findByIsinCdNm(bond.getName());
                if (bondProduct == null) continue;

                BondProductPrice bondProductPrice = bondProduct.getBondProductPrice();
                if (bondProductPrice == null) continue;

                double currentPrice;
                if (i == 0) {
                    // 현재 가격 (BondProduct의 price 사용)
                    currentPrice = bondProduct.getPrice();
                } else {
                    // i개월 전 가격
                    currentPrice = getPriceForMonth(bondProductPrice, i);
                }

                double purchasePrice = bond.getPrice();

                if (purchasePrice > 0 && currentPrice > 0) {
                    double bondReturn = ((currentPrice / purchasePrice) * 100) - 100;
                    totalBondReturn += bondReturn;
                    bondCount++;
                }
            }

            if (bondCount > 0) {
                double averageBondReturn = totalBondReturn / bondCount;
                bondReturns.add(new BondReturnDTO(i + 1, Double.parseDouble(df.format(averageBondReturn))));
            } else {
                bondReturns.add(new BondReturnDTO(i + 1, 0.0));
            }
        }

        return bondReturns;
    }

    private double getPriceForMonth(BondProductPrice bondProductPrice, int monthsAgo) {
        return switch (monthsAgo) {
            case 1 -> bondProductPrice.getOneMonthAgoPrice();
            case 2 -> bondProductPrice.getTwoMonthsAgoPrice();
            case 3 -> bondProductPrice.getThreeMonthsAgoPrice();
            case 4 -> bondProductPrice.getFourMonthsAgoPrice();
            case 5 -> bondProductPrice.getFiveMonthsAgoPrice();
            default -> 0;
        };
    }

    // Spot 자산 합산 메서드
    public long calculateSpotAssetsSum(Member uid) {
        List<Spot> spotData = spotRepository.findByUidAndDeleteDateIsNull(uid);
        if (spotData.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_SPOT, "user dont have spots");
        }
        return spotData.stream().mapToLong(Spot::getPrice).sum();
    }

    // Spot 자산 월별 합산 메서드
    private long calculateSpotAssetsSumBefore(Member uid, int monthsAgo) {
        List<Object[]> spotAssets = spotRepository.findMonthlySpotAssets(uid);
        if (spotAssets.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_SPOT, "user dont have spots");
        }
        return spotAssets.stream()
                .filter(row -> (int) row[0] == monthsAgo)  // month 필터링
                .mapToLong(row -> ((BigDecimal) row[1]).longValue())  // totalAmount 계산
                .sum();
    }

    public List<MonthlyBalanceDTO> getMonthlyIncomeOutcomeBalance(int uid) {
        List<Object[]> incomeResults = incomeRepository.findMonthlyIncomeByUid(uid);
        if (incomeResults.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INCOME, "user dont have incomes");
        }

        List<Object[]> outcomeResults = outComeUserRepository.findMonthlyOutcomeByUid(uid);
        if (outcomeResults.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_OUTCOME, "user dont have outcomes");
        }

        TreeMap<String, MonthlyBalanceDTO> balanceMap = new TreeMap<>();

        for (Object[] income : incomeResults) {
            String month = (String) income[0];
            long totalIncome = ((BigDecimal) income[1]).longValueExact();
            balanceMap.put(month, new MonthlyBalanceDTO(month, totalIncome, 0L, totalIncome,(double) 100));
        }

        for (Object[] outcome : outcomeResults) {
            String month = (String) outcome[0];
            long totalOutcome = ((BigDecimal) outcome[1]).longValueExact();
            if (balanceMap.containsKey(month)) {
                MonthlyBalanceDTO dto = balanceMap.get(month);
                dto.setTotalOutcome(totalOutcome);
                dto.setBalance(dto.getTotalIncome() - totalOutcome);
                // totalIncome을 double로 변환하여 나눗셈에서 소수점 이하까지 계산
                if (dto.getTotalIncome() > 0) {
                    dto.setBalalnceRate((double)(dto.getTotalIncome() - totalOutcome) / dto.getTotalIncome() * 100);
                } else {
                    dto.setBalalnceRate((double) 0); // totalIncome이 0일 경우 0으로 설정
                }
            } else {
                balanceMap.put(month,
                        new MonthlyBalanceDTO(month, 0L, totalOutcome, totalOutcome,(double) 0));
            }
        }

        return new ArrayList<>(balanceMap.values());
    }

    public Map<String, Object> compareAssetsWithAgeGroup(int uid) {

        // 1. 현재 사용자의 uid를 기반으로 나이 정보 추출
        Member member = memberService.findMemberByUid(uid);

        int birthYear = member.getBirth_year();

        // 2. 10대, 20대 구분 (예: 10대는 2000~2009년 출생, 20대는 1990~1999년 출생)
        int lowerBoundYear = birthYear - (birthYear % 10); // 연령대 시작 연도
        int upperBoundYear = lowerBoundYear + 9; // 연령대 끝 연도

        // 3. 해당 연령대의 평균 자산 구하기
        Long avgAmount = assetSummaryRepository.findAverageAmountByAgeRange(lowerBoundYear, upperBoundYear);

        // 4. 현재 사용자의 자산 정보 구하기
        AssetSummary userAssetSummary = assetSummaryRepository.findLatestByUid(member);

        if (userAssetSummary == null) {
            throw new CustomException(ErrorCode.ASSET_SUMMARY_NOT_FOUND, "No asset summary found for user: " + uid);
        }

        Long userTotalAmount = userAssetSummary.getTotalAmount();

        // 5. 차이 계산
        Long deferAmount = userTotalAmount - avgAmount;

        // 6. 결과값 반환
        Map<String, Object> response = new HashMap<>();
        response.put("bsAmount", userTotalAmount);
        response.put("spotAvgAmount", avgAmount);
        response.put("defer", deferAmount);

        return response;
    }

    @Override
    public List<Map<String, Object>> compareAssetsByCategoryWithAgeGroup(int uid) {
        // 1. 현재 사용자의 uid를 기반으로 나이 정보 추출
        Member member = memberService.findMemberByUid(uid);

        int birthYear = member.getBirth_year();

        // 2. 10대, 20대 구분
        int lowerBoundYear = birthYear - (birthYear % 10);
        int upperBoundYear = lowerBoundYear + 9;

        // 3. 자산 요약 가져오기
        AssetSummary userAssetSummary = assetSummaryRepository.findLatestByUid(member);
        if (userAssetSummary == null) {
            throw new CustomException(ErrorCode.ASSET_SUMMARY_NOT_FOUND, "No asset summary found for user: " + uid);
        }

        // 4. 자산 카테고리별로 비교
        List<Map<String, Object>> assetComparisonList = new ArrayList<>();
        addComparisonData("bond", userAssetSummary.getBond(), lowerBoundYear, upperBoundYear, assetComparisonList);
        addComparisonData("deposit", userAssetSummary.getDeposit(), lowerBoundYear, upperBoundYear, assetComparisonList);
        addComparisonData("saving", userAssetSummary.getSaving(), lowerBoundYear, upperBoundYear, assetComparisonList);
        addComparisonData("subscription", userAssetSummary.getSubscription(), lowerBoundYear, upperBoundYear, assetComparisonList);
        addComparisonData("withdrawal", userAssetSummary.getWithdrawal(), lowerBoundYear, upperBoundYear, assetComparisonList);
        addComparisonData("stock", userAssetSummary.getStock(), lowerBoundYear, upperBoundYear, assetComparisonList);
        addComparisonData("cash", userAssetSummary.getCash(), lowerBoundYear, upperBoundYear, assetComparisonList);
        addComparisonData("coin", userAssetSummary.getCoin(), lowerBoundYear, upperBoundYear, assetComparisonList);

        return assetComparisonList;
    }

    private void addComparisonData(String category, Long bsAmount, int lowerBoundYear, int upperBoundYear,
                                   List<Map<String, Object>> assetComparisonList) {
        if (bsAmount == null) {
            bsAmount = 0L;
        }

        // 연령대의 카테고리별 평균 자산 구하기
        Long avgAmount = assetSummaryRepository.findAverageAmountByAgeRangeAndCategory(lowerBoundYear, upperBoundYear, category);
        Long deferAmount = bsAmount - avgAmount;

        // 각 카테고리별 결과값 추가
        Map<String, Object> comparisonData = new HashMap<>();
        comparisonData.put("bsAmount", bsAmount);
        comparisonData.put("categoryAvgAmount", avgAmount);
        comparisonData.put("defer", deferAmount);
        comparisonData.put("category", category);

        assetComparisonList.add(comparisonData);
    }

    private Date getDateBeforeMonths(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, -months);
        return cal.getTime();
    }

    public List<BondProduct> findBondProductsWithNonNullPrices() {
        List<BondProductPrice> bondProductPrices = bondProductPriceRepository.findAllNonNullValues();
        List<String> isinCds = bondProductPrices.stream()
                .map(BondProductPrice::getIsinCd)
                .collect(Collectors.toList());
        return bondProductRepository.findByBondProductPriceIsinCdIn(isinCds);
    }

    public List<StockProduct> findStockProducts() {
        return stockProductRepository.findOrderByPriceDesc();
    }

    public List<StockProduct> findStockProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit); // 첫 번째 페이지부터 시작, 최대 'limit'만큼 가져옴
        Page<StockProduct> stockProductPage = stockProductRepository.findStockProductsWithPaging(pageable);
        return stockProductPage.getContent(); // 결과를 리스트로 변환
    }

    public List<CoinProduct> findCoinProducts() {
        return coinRepository.findOrderByClosingPriceDesc();
    }

    // AssetSummary업데이트 스케쥴러
    @Scheduled(cron = "0 0 06 1 * ?") // 매월 1일 오전 6시에 실행
    @Transactional
    public void scheduleAssetSummaryUpdate() {
        log.info("Starting scheduled asset summary update for all users");
        memberRepository.findAll().forEach(member -> {
            try {
                assetSummaryRepository.insertOrUpdateAssetSummary(member.getUid());
                //assetSummaryRepository.deleteDuplicateAssetSummary();
                log.info("Updated asset summary for user: {}", member.getUid());
            } catch (Exception e) {
                log.error("Error updating asset summary for user: {}", member.getUid(), e);
                throw new CustomException(ErrorCode.DATABASE_UPDATE_ERROR,
                        "Failed to update asset summary for user: " + member.getUid());
            }
        });
        log.info("Completed scheduled asset summary update for all users");
    }

}