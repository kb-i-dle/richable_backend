package com.idle.kb_i_dle_backend.domain.finance.service;

import com.idle.kb_i_dle_backend.domain.finance.dto.*;
import com.idle.kb_i_dle_backend.domain.finance.entity.*;
import com.idle.kb_i_dle_backend.domain.income.repository.IncomeRepository;
import com.idle.kb_i_dle_backend.domain.outcome.repository.OutcomeUserRepository;
import com.idle.kb_i_dle_backend.domain.member.repository.MemberRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.*;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;

import java.math.BigDecimal;
import java.text.DecimalFormat;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FinanceServiceImpl implements FinanceService {

    private final BankRepository bankRepository;
    private final BondRepository bondRepository;
    private final CoinRepository coinRepository;
    private final SpotRepository spotRepository;
    private final StockRepository stockRepository;
    private final IncomeRepository incomeRepository;
    private final OutcomeUserRepository outComeUserRepository;
    private final MemberRepository memberRepository;
    private final AssetSummaryRepository assetSummaryRepository;

    // 소수점 이하 한 자리로 포맷팅할 수 있는 DecimalFormat
    private static final DecimalFormat df = new DecimalFormat("#.#");

    // 금융 자산 합계 계산
    @Override
    public FinancialSumDTO getFinancialAssetsSum(int uid) {
        Optional<Member> member = memberRepository.findByUid(uid);
        Long financialAssetsSum = calculateFinancialAssetsSum(member);
        return new FinancialSumDTO(financialAssetsSum);
    }

    // 총 자산 합계 계산 (금융 자산 + Spot 자산)
    @Override
    public FinancialSumDTO getTotalAssetsSum(int uid) {
        Optional<Member> member = memberRepository.findByUid(uid);
        Long totalAssetsSum =
                calculateFinancialAssetsSum(member) + calculateSpotAssetsSum(member);
        return new FinancialSumDTO(totalAssetsSum);
    }

    @Override
    public FinancialSumDTO getAssetSummeryByDateBefore(int uid, Date date) throws Exception{
        Optional<Member> member = Optional.of(memberRepository.findByUid(uid).orElseThrow());
        AssetSummary assetSummary = assetSummaryRepository.findFirstByUidAndUpdateDateBeforeOrderByUpdateDateDesc(member, date);
        return new FinancialSumDTO(assetSummary.getTotalAmount());
    }

    // 금융 자산 목록 조회
    @Override
    public List<AssetDTO> getFinancialAsset(int uid) {
        Optional<Member> member = memberRepository.findByUid(uid);
        List<AssetDTO> assetList = new ArrayList<>();

        assetList.add(new AssetDTO("예적금", sumBankAssets(member)));
        assetList.add(new AssetDTO("주식", sumStockAssets(member)));
        assetList.add(new AssetDTO("코인", sumCoinAssets(member)));
        //채권은 매입가격으로 계산 (종가가 없음)
        assetList.add(new AssetDTO("채권", sumBondAssets(member)));

        return assetList;
    }

    // 6개월 금융 자산 변동 계산
    @Override
    public List<FinancialChangeDTO> getSixMonthFinancialChanges(int uid) {
        Optional<Member> member = memberRepository.findByUid(uid);
        List<FinancialChangeDTO> financialChanges = new ArrayList<>();
        member.ifPresent(m -> {
            for (int i = 1; i < 7; i++) {
                long monthlySum =
                        (i == 1) ? calculateFinancialAssetsSum(Optional.of(m)) : calculateMonthlyAssetsSum(m, i);
                financialChanges.add(new FinancialChangeDTO(i, monthlySum));
            }
        });
        return financialChanges;
    }

    // 6개월 총 자산 변동 계산 (금융 자산 + Spot 자산)
    @Override
    public List<TotalChangeDTO> getSixMonthTotalChanges(int uid) {
        Optional<Member> member = memberRepository.findByUid(uid);
        List<TotalChangeDTO> totalChanges = new ArrayList<>();
        member.ifPresent(m -> {
            for (int i = 1; i < 7; i++) {
                long monthlySum = (i == 1) ? (calculateFinancialAssetsSum(Optional.of(m)) + calculateSpotAssetsSum(
                        Optional.of(m)))
                        : calculateMonthlyAssetsSum(m, i) + calculateSpotAssetsSumBefore(m, i);
                totalChanges.add(new TotalChangeDTO(i, monthlySum));
            }
        });
        return totalChanges;
    }

    //6개월 간 저축률
    @Override
    public List<MonthlySavingRateDTO> getMonthlySavingRateTrend(int uid) {
        Optional<Member> member = memberRepository.findByUid(uid);
        List<MonthlySavingRateDTO> monthlySavingRates = new ArrayList<>();

//        for (int i = 0; i < 6; i++) {
//            // 해당 월의 시작일과 마지막일 계산
//            LocalDate startOfMonth = LocalDate.now().minusMonths(i).withDayOfMonth(1);
//            LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
//
//            Date startDate = Date.from(startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
//            Date endDate = Date.from(endOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//            // 소득과 소비 데이터를 가져옴
//            List<Income> incomes = incomeRepository.findByUidAndDateBetween(uid, startDate, endDate);
//            List<OutcomeUser> outComeUsers = outComeUserRepository.findByUidAndDateBetween(uid, startDate, endDate);
//
//            // 총 소득 계산
//            long totalIncome = incomes.stream().mapToLong(Income::getAmount).sum();
//
//            // 총 소비량 계산
//            long totalConsumption = outComeUsers.stream().mapToLong(OutcomeUser::getAmount).sum();
//
//            // 저축 가능 금액 계산 (소득 - 소비)
//            long savings = totalIncome - totalConsumption;
//
//            // 저축률 계산: 저축 가능 금액 / 총 소득 (소득이 0인 경우 저축률을 0으로 설정)
//            double savingRate = totalIncome > 0 ? (double) savings / totalIncome : 0;
//
//            System.out.println(savings);
//            System.out.println(totalIncome);
//
//            // 월별 저축률 DTO에 추가
//            savingRate = Math.floor(savingRate * 10) / 10.0;
//            monthlySavingRates.add(new MonthlySavingRateDTO(i,savingRate));
//        }

        return monthlySavingRates;
    }

    @Override
    public List<StockReturnDTO> getStockReturnTrend(int uid) {
//        Optional<Member> member = userRepository.findByUid(uid);
        return memberRepository.findByUid(uid)
                .map(member -> {
                    List<StockReturnDTO> stockReturns = new ArrayList<>();
                    List<Stock> stocks = stockRepository.findAllByUidAndDeleteDateIsNull(member);

                    for (int i = 1; i < 7; i++) {
                        double totalStockReturn = 0;
                        int stockCount = 0;

                        for (Stock stock : stocks) {
                            Date purchaseDateAsDate = stock.getAddDate();
                            LocalDate purchaseDate = purchaseDateAsDate.toInstant().atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                            LocalDate currentMonthDate = LocalDate.now().minusMonths(i);
                            Date endDate = Date.from(currentMonthDate.withDayOfMonth(currentMonthDate.lengthOfMonth())
                                    .atStartOfDay(ZoneId.systemDefault()).toInstant());

                            if (i == 1) {
                                // 현재 가격과 매입 가격 비교하여 수익률 계산
                                double currentPrice = 50000; // 임의로 설정된 현재 가격
                                double purchasePrice = stockRepository.getStockPriceByPdno(stock.getPdno());

                                if (purchasePrice > 0) {
                                    double stockReturn = ((currentPrice / purchasePrice) * 100) - 100;
                                    totalStockReturn += stockReturn;
                                    stockCount++;
                                }
                            } else {
                                // 특정 달의 가격으로 수익률 계산
                                if (!purchaseDate.isAfter(currentMonthDate)) {
                                    Double currentMonthPrice = stockRepository.getStockPriceForMonth(stock.getPdno(),
                                            i);
                                    double purchasePrice = stockRepository.getStockPriceByPdno(stock.getPdno());

                                    if (currentMonthPrice != null && purchasePrice > 0) {
                                        double stockReturn = ((currentMonthPrice / purchasePrice) * 100) - 100;
                                        totalStockReturn += stockReturn;
                                        stockCount++;
                                    }
                                }
                            }
                        }

                        if (stockCount > 0) {
                            double averageStockReturn = totalStockReturn / stockCount;
                            stockReturns.add(new StockReturnDTO(i, Double.parseDouble(df.format(averageStockReturn))));
                        } else {
                            stockReturns.add(new StockReturnDTO(i, 0));
                        }
                    }

                    return stockReturns;
                })
                .orElse(Collections.emptyList());
    }


    //6개월 코인 수익률
    @Override
    public List<CoinReturnDTO> getCoinReturnTrend(int uid) {
        List<CoinReturnDTO> coinReturns = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("#.##");
        Optional<Member> member = memberRepository.findByUid(uid);

        for (int i = 1; i < 7; i++) {
            double totalCoinReturn = 0;
            int coinCount = 0;

            List<Coin> coins = coinRepository.findByUid(member);

            for (Coin coin : coins) {
                Date purchaseDateAsDate = coin.getAddDate();
                LocalDate purchaseDate = purchaseDateAsDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate currentMonthDate = LocalDate.now().minusMonths(i);
                Date endDate = Date.from(currentMonthDate.withDayOfMonth(currentMonthDate.lengthOfMonth())
                        .atStartOfDay(ZoneId.systemDefault()).toInstant());

                if (i == 1) {
                    // 현재 가격과 매입 가격 비교하여 수익률 계산
                    double currentPrice = 30000; // 임의로 설정된 현재 가격
                    double purchasePrice = coinRepository.findCoinPriceBy(coin.getCurrency());

                    if (purchasePrice > 0) {
                        double coinReturn = ((currentPrice / purchasePrice) * 100) - 100;
                        totalCoinReturn += coinReturn;
                        coinCount++;
                    }
                } else {
                    // 특정 달의 가격으로 수익률 계산
                    if (!purchaseDate.isAfter(currentMonthDate)) {
                        Double currentMonthPrice = coinRepository.getCoinPriceForMonth(coin.getCurrency(), i);
                        double purchasePrice = coinRepository.findCoinPriceBy(coin.getCurrency());

                        if (currentMonthPrice != null && purchasePrice > 0) {
                            double coinReturn = ((currentMonthPrice / purchasePrice) * 100) - 100;
                            totalCoinReturn += coinReturn;
                            coinCount++;
                        }
                    }
                }
            }

            if (coinCount > 0) {
                double averageCoinReturn = totalCoinReturn / coinCount;
                coinReturns.add(new CoinReturnDTO(i, Double.parseDouble(df.format(averageCoinReturn))));
            } else {
                coinReturns.add(new CoinReturnDTO(i, 0.0));
            }
        }
        return coinReturns;
    }

    //6개월 채권 수익률
    @Override
    public List<BondReturnDTO> getBondReturnTrend(int uid) {
        List<BondReturnDTO> bondReturns = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("#.##");
        Optional<Member> member = memberRepository.findByUid(uid);

        for (int i = 1; i < 7; i++) {
            double totalBondReturn = 0;
            int bondCount = 0;

            List<Bond> bonds = bondRepository.findByUid(member);

            for (Bond bond : bonds) {
                Date purchaseDateAsDate = bond.getAddDate();
                LocalDate purchaseDate = purchaseDateAsDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate currentMonthDate = LocalDate.now().minusMonths(i);
                Date endDate = Date.from(currentMonthDate.withDayOfMonth(currentMonthDate.lengthOfMonth())
                        .atStartOfDay(ZoneId.systemDefault()).toInstant());

                if (i == 1) {
                    // 현재 가격과 매입 가격 비교하여 수익률 계산
                    double currentPrice = 100000; // 임의로 설정된 현재 가격
                    double purchasePrice = bondRepository.
                            getPriceByName(bond.getName());

                    if (purchasePrice > 0) {
                        double bondReturn = ((currentPrice / purchasePrice) * 100) - 100;
                        totalBondReturn += bondReturn;
                        bondCount++;
                    }
                } else {
                    // 특정 달의 가격으로 수익률 계산
                    if (!purchaseDate.isAfter(currentMonthDate)) {
                        Double currentMonthPrice = bondRepository.getBondPriceForMonth(bond.getName(), i);
                        double purchasePrice = bondRepository.getPriceByName(bond.getName());

                        if (currentMonthPrice != null && purchasePrice > 0) {
                            double bondReturn = ((currentMonthPrice / purchasePrice) * 100) - 100;
                            totalBondReturn += bondReturn;
                            bondCount++;
                        }
                    }
                }
            }

            if (bondCount > 0) {
                double averageBondReturn = totalBondReturn / bondCount;
                bondReturns.add(new BondReturnDTO(i, Double.parseDouble(df.format(averageBondReturn))));
            } else {
                bondReturns.add(new BondReturnDTO(i, 0.0));
            }
        }
        return bondReturns;
    }


    // Helper 메서드: 다양한 타입을 double로 변환
    private double convertToDouble(Object obj) {
        if (obj instanceof Integer) {
            return ((Integer) obj).doubleValue();
        } else if (obj instanceof Double) {
            return (Double) obj;
        } else {
            return Double.parseDouble(obj.toString());
        }
    }

    private long calculateFinancialAssetsSum(Optional<Member> uid) {
        return sumBankAssets(uid) + sumBondAssets(uid) + sumStockAssets(uid) + sumCoinAssets(uid);
    }


    // 은행 자산 합계
    private long sumBankAssets(Optional<Member> uid) {
        return bankRepository.findAllByUidAndDeleteDateIsNull(uid).stream().mapToLong(Bank::getBalanceAmt).sum();
    }

    // 주식 자산 합계
    public long sumStockAssets(Optional<Member> memberOpt) {
        return memberOpt.map(member ->
                stockRepository.getStockBalanceAndPrice(member.getUid()).stream()
                        .mapToLong(row -> (long) (convertToDouble(row[0]) * convertToDouble(row[1])))
                        .sum()
        ).orElse(0L);
    }

    // 코인 자산 합계
    private long sumCoinAssets(Optional<Member> uid) {
        return coinRepository.findCoinBalanceAndPriceByUid(uid).stream()
                .mapToLong(row -> (long) (convertToDouble(row[0]) * convertToDouble(row[1]))).sum();
    }

    // 채권 자산 합계
    private long sumBondAssets(Optional<Member> uid) {
        return bondRepository.findAllByUidAndDeleteDateIsNull(uid).stream()
                .mapToLong(bond -> (long) bond.getPrice() * bond.getCnt()).sum();
    }

    // Spot 자산 합산 메서드
    private long calculateSpotAssetsSum(Optional<Member> uid) {
        return uid.map(member -> {
            List<Spot> spotData = spotRepository.findByUidAndDeleteDateIsNull(member);
            return spotData.stream().mapToLong(Spot::getPrice).sum();
        }).orElse(0L);
    }

    // 월별 금융 자산 합산 메서드
    private long calculateMonthlyAssetsSum(Member uid, int monthsAgo) {
        // 기존
        //        LocalDateTime endOfMonthLDT = LocalDate.now().minusMonths(monthsAgo).withDayOfMonth(1).atStartOfDay();
        //        Date endOfMonth = Date.from(endOfMonthLDT.atZone(ZoneId.systemDefault()).toInstant());
        //
        //        long monthlySum = 0;
        //
        //        // 은행 자산 합산
        //        List<Bank> bankAssets = bankRepository.findAllByUidAndAddDateBefore(uid, endOfMonth);
        //        monthlySum += bankAssets.stream().mapToLong(Bank::getBalanceAmt).sum();
        //
        //        // 채권 자산 합산
        //        List<Bond> bondAssets = bondRepository.findAllByUidAndAddDateBefore(uid, monthsAgo);
        //        monthlySum += bondAssets.stream().mapToLong(Bond -> (long) Bond.getPrice() * Bond.getCnt()).sum();
        //
        //        // 가상화폐 자산 합산
        //        List<Object[]> coinData = coinRepository.findCoinBalanceAndPriceByUserIdAndBefore(uid, endOfMonth, monthsAgo);
        //        monthlySum += coinData.stream()
        //                .mapToLong(row -> (long) ((double) row[0] * Integer.parseInt((String) row[1])))
        //                .sum();
        //
        //        // 주식 자산 합산
        //        List<Object[]> stockData = stockRepository.getStockBalanceAndClosingPriceBefore(uid, monthsAgo);
        //        monthlySum += stockData.stream()
        //                .mapToLong(row -> (long) ((double) row[0] * (double) row[1]))
        //                .sum();
        //
        //        return monthlySum;
            long monthlySum = 0;

            // 은행 자산 합산
            List<Object[]> bankAssets = bankRepository.findMonthlyBankAssets(uid);
            monthlySum += bankAssets.stream()
                    .filter(row -> (int) row[0] == monthsAgo)  // month 필터링
                    .mapToLong(row -> (long) row[2])  // totalAmount 계산
                    .sum();

            // 채권 자산 합산
            List<Object[]> bondAssets = bondRepository.findMonthlyBondAssets(uid);
            monthlySum += bondAssets.stream()
                    .filter(row -> (int) row[0] == monthsAgo)  // month 필터링
                    .mapToLong(row -> ((long) row[2]))  // totalAmount 계산
                    .sum();

            // 가상화폐 자산 합산
            List<Object[]> coinAssets = coinRepository.findMonthlyCoinAssets(uid);
            monthlySum += coinAssets.stream()
                    .filter(row -> (int) row[0] == monthsAgo)  // month 필터링
                    .mapToLong(row -> ((long) row[2]))  // totalAmount 계산
                    .sum();

            // 주식 자산 합산
            List<Object[]> stockAssets = stockRepository.findMonthlyStockAssets(uid);
            monthlySum += stockAssets.stream()
                    .filter(row -> (int) row[0] == monthsAgo)  // month 필터링
                    .mapToLong(row -> ((long) row[2]))  // totalAmount 계산
                    .sum();

            return monthlySum;
    }

    // Spot 자산 월별 합산 메서드
    private long calculateSpotAssetsSumBefore(Member uid, int monthsAgo) {

        // 기존
        //        LocalDateTime endOfMonthLDT = LocalDate.now().minusMonths(monthsAgo).withDayOfMonth(1).atStartOfDay();
        //        Date endOfMonth = Date.from(endOfMonthLDT.atZone(ZoneId.systemDefault()).toInstant());
        //        List<Spot> spotAssets = spotRepository.findByUidAndAddDateBefore(uid, endOfMonth);
        //        return spotAssets.stream().mapToLong(Spot::getPrice).sum();
            List<Object[]> spotAssets = spotRepository.findMonthlySpotAssets(uid);
            return spotAssets.stream()
                    .filter(row -> (int) row[0] == monthsAgo)  // month 필터링
                    .mapToLong(row -> ((long) row[1]))  // totalAmount 계산
                    .sum();
        }

    public List<MonthlyBalanceDTO> getMonthlyIncomeOutcomeBalance(int uid) {
        List<Object[]> incomeResults = incomeRepository.findMonthlyIncomeByUid(uid);
        List<Object[]> outcomeResults = outComeUserRepository.findMonthlyOutcomeByUid(uid);

        Map<String, MonthlyBalanceDTO> balanceMap = new HashMap<>();

        for (Object[] income : incomeResults) {
            String month = (String) income[0];
            long totalIncome = (long) income[1];
            balanceMap.put(month, new MonthlyBalanceDTO(month, totalIncome, 0L, totalIncome));
        }

        for (Object[] outcome : outcomeResults) {
            String month = (String) outcome[0];
            long totalOutcome = (long) outcome[1];
            if (balanceMap.containsKey(month)) {
                MonthlyBalanceDTO dto = balanceMap.get(month);
                dto.setTotalOutcome(totalOutcome);
                dto.setBalance(dto.getTotalIncome()-totalOutcome);
            } else {
                balanceMap.put(month,
                        new MonthlyBalanceDTO(month, 0L, totalOutcome, totalOutcome));
            }
        }

        return new ArrayList<>(balanceMap.values());
    }

    public Map<String, Object> compareAssetsWithAgeGroup(int uid) {

        // 1. 현재 사용자의 uid를 기반으로 나이 정보 추출
        Optional<Member> member = memberRepository.findByUid(uid);

        if (!member.isPresent()) {
            throw new IllegalArgumentException("Member not found for uid: " + uid);
        }

        int birthYear = member.get().getBirth_year();

        // 2. 10대, 20대 구분 (예: 10대는 2000~2009년 출생, 20대는 1990~1999년 출생)
        int lowerBoundYear = birthYear - (birthYear % 10); // 연령대 시작 연도
        int upperBoundYear = lowerBoundYear + 9; // 연령대 끝 연도

        // 3. 해당 연령대의 평균 자산 구하기
        long avgAmount = assetSummaryRepository.findAverageAmountByAgeRange(lowerBoundYear, upperBoundYear);

        // 4. 현재 사용자의 자산 정보 구하기
        AssetSummary userAssetSummary = assetSummaryRepository.findLatestByUid(member);
        Long userTotalAmount = userAssetSummary.getTotalAmount();

        // 5. 차이 계산
        Long deferAmount = userTotalAmount- avgAmount;

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
        Optional<Member> member = memberRepository.findByUid(uid);
        if (!member.isPresent()) {
            throw new IllegalArgumentException("Member not found for uid: " + uid);
        }
        int birthYear = member.get().getBirth_year();

        // 2. 10대, 20대 구분
        int lowerBoundYear = birthYear - (birthYear % 10);
        int upperBoundYear = lowerBoundYear + 9;

        // 3. 자산 요약 가져오기
        AssetSummary userAssetSummary = assetSummaryRepository.findLatestByUid(member);
        if (userAssetSummary == null) {
            throw new IllegalArgumentException("Asset summary not found for uid: " + uid);
        }

        // 4. 자산 카테고리별로 비교
        List<Map<String, Object>> assetComparisonList = new ArrayList<>();
        addComparisonData("채권", userAssetSummary.getBond(), lowerBoundYear, upperBoundYear, assetComparisonList);
        addComparisonData("예금", userAssetSummary.getDeposit(), lowerBoundYear, upperBoundYear, assetComparisonList);
        addComparisonData("적금", userAssetSummary.getSaving(), lowerBoundYear, upperBoundYear, assetComparisonList);
        addComparisonData("청약", userAssetSummary.getSubscription(), lowerBoundYear, upperBoundYear, assetComparisonList);
        addComparisonData("입출금", userAssetSummary.getWithdrawal(), lowerBoundYear, upperBoundYear, assetComparisonList);
        addComparisonData("주식", userAssetSummary.getStock(), lowerBoundYear, upperBoundYear, assetComparisonList);
        addComparisonData("현금", userAssetSummary.getCash(), lowerBoundYear, upperBoundYear, assetComparisonList);
        addComparisonData("코인", userAssetSummary.getCoin(), lowerBoundYear, upperBoundYear, assetComparisonList);

        return assetComparisonList;
    }

    private void addComparisonData(String category, Long bsAmount, int lowerBoundYear, int upperBoundYear, List<Map<String, Object>> assetComparisonList) {
        if (bsAmount == null) {
            bsAmount = 0L;
        }

        // 연령대의 평균 자산 구하기
        Long avgAmount = assetSummaryRepository.findAverageAmountByAgeRange(lowerBoundYear, upperBoundYear);
        Long deferAmount = bsAmount-avgAmount;

        // 각 카테고리별 결과값 추가
        Map<String, Object> comparisonData = new HashMap<>();
        comparisonData.put("bsAmount", bsAmount);
        comparisonData.put("spotAvgAmount", avgAmount);
        comparisonData.put("defer", deferAmount);
        comparisonData.put("category", category);

        assetComparisonList.add(comparisonData);
    }


}