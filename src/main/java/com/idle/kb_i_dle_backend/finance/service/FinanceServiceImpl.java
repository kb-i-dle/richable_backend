package com.idle.kb_i_dle_backend.finance.service;

import com.idle.kb_i_dle_backend.finance.dto.*;
import com.idle.kb_i_dle_backend.finance.entity.*;
import com.idle.kb_i_dle_backend.finance.repository.*;
import com.idle.kb_i_dle_backend.member.entity.User;
import java.text.DecimalFormat;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FinanceServiceImpl implements FinanceService {

    @Autowired
    private BankRepository bankRepository;
    @Autowired
    private BondRepository bondRepository;
    @Autowired
    private CoinRepository coinRepository;
    @Autowired
    private SpotRepository spotRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private IncomeRepository incomeRepository;
    @Autowired
    private OutComeUserRepository outComeUserRepository;

    // 소수점 이하 한 자리로 포맷팅할 수 있는 DecimalFormat
    private static final DecimalFormat df = new DecimalFormat("#.#");

    // 금융 자산 합계 계산
    @Override
    public FinancialSumDTO getFinancialAssetsSum(int uid) {
        long financialAssetsSum = calculateFinancialAssetsSum(uid);
        return new FinancialSumDTO(financialAssetsSum);
    }

    // 총 자산 합계 계산 (금융 자산 + Spot 자산)
    @Override
    public FinancialSumDTO getTotalAssetsSum(int uid) {
        long totalAssetsSum = calculateFinancialAssetsSum(uid) + calculateSpotAssetsSum(uid);
        return new FinancialSumDTO(totalAssetsSum);
    }

    // 금융 자산 목록 조회
    @Override
    public List<AssetDTO> getFinancialAsset(int uid) {
        List<AssetDTO> assetList = new ArrayList<>();
        assetList.add(new AssetDTO("예적금", sumBankAssets(uid)));
        assetList.add(new AssetDTO("주식", sumStockAssets(uid)));
        assetList.add(new AssetDTO("코인", sumCoinAssets(uid)));

        //채권은 매입가격으로 계산 (종가가 없음)
        assetList.add(new AssetDTO("채권", sumBondAssets(uid)));
        return assetList;
    }

    // 6개월 금융 자산 변동 계산
    @Override
    public List<FinancialChangeDTO> getSixMonthFinancialChanges(int uid) {
        List<FinancialChangeDTO> financialChanges = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            long monthlySum = (i == 0) ? calculateFinancialAssetsSum(uid) : calculateMonthlyAssetsSum(uid, i);
            financialChanges.add(new FinancialChangeDTO(i, monthlySum));
        }
        return financialChanges;
    }

    // 6개월 총 자산 변동 계산 (금융 자산 + Spot 자산)
    @Override
    public List<TotalChangeDTO> getSixMonthTotalChanges(int uid) {
        List<TotalChangeDTO> totalChanges = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            long monthlySum = (i == 0) ? (calculateFinancialAssetsSum(uid) + calculateSpotAssetsSum(uid))
                    : calculateMonthlyAssetsSum(uid, i)+ calculateSpotAssetsSumBefore(uid,i);
            totalChanges.add(new TotalChangeDTO(i, monthlySum));
        }
        return totalChanges;
    }

    //6개월 간 저축률
    @Override
    public List<MonthlySavingRateDTO> getMonthlySavingRateTrend(int uid) {
        List<MonthlySavingRateDTO> monthlySavingRates = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            // 해당 월의 시작일과 마지막일 계산
            LocalDate startOfMonth = LocalDate.now().minusMonths(i).withDayOfMonth(1);
            LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

            Date startDate = Date.from(startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(endOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // 소득과 소비 데이터를 가져옴
            List<Income> incomes = incomeRepository.findByUidAndDateBetween(uid, startDate, endDate);
            List<OutComeUser> outComeUsers = outComeUserRepository.findByUidAndDateBetween(uid, startDate, endDate);

            // 총 소득 계산
            long totalIncome = incomes.stream().mapToLong(Income::getAmount).sum();

            // 총 소비량 계산
            long totalConsumption = outComeUsers.stream().mapToLong(OutComeUser::getAmount).sum();

            // 저축 가능 금액 계산 (소득 - 소비)
            long savings = totalIncome - totalConsumption;

            // 저축률 계산: 저축 가능 금액 / 총 소득 (소득이 0인 경우 저축률을 0으로 설정)
            double savingRate = totalIncome > 0 ? (double) savings / totalIncome : 0;

            System.out.println(savings);
            System.out.println(totalIncome);

            // 월별 저축률 DTO에 추가
            savingRate = Math.floor(savingRate * 10) / 10.0;
            monthlySavingRates.add(new MonthlySavingRateDTO(i,savingRate));
        }

        return monthlySavingRates;
    }

    @Override
    public List<StockReturnDTO> getStockReturnTrend(int uid) {
        List<StockReturnDTO> stockReturns = new ArrayList<>();
        List<Stock> userStocks = stockRepository.findAllByUidAndDeleteDateIsNull(uid);

        for (int i = 0; i < 6; i++) {
            double totalStockReturn = 0;
            int stockCount = 0;

            for (Stock stock : userStocks) {
                Date purchaseDateAsDate = stock.getAddDate();
                LocalDate purchaseDate = purchaseDateAsDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate currentMonthDate = LocalDate.now().minusMonths(i);
                Date endDate = Date.from(currentMonthDate.withDayOfMonth(currentMonthDate.lengthOfMonth())
                        .atStartOfDay(ZoneId.systemDefault()).toInstant());

                if (i == 0) {
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
                        Double currentMonthPrice = stockRepository.getStockPriceForMonth(stock.getPdno(), endDate, i);
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
    }


    //6개월 코인 수익률
    @Override
    public List<CoinReturnDTO> getCoinReturnTrend(int uid) {

        List<CoinReturnDTO> coinReturns = new ArrayList<>();
        List<Coin> userCoins = coinRepository.findAllByUidAndDeleteDateIsNull(uid);

        for (int i = 0; i < 6; i++) {
            double totalCoinReturn = 0;
            int coinCount = 0;


            for (Coin coin : userCoins) {
                Date purchaseDateAsDate = coin.getAddDate();
                LocalDate purchaseDate = purchaseDateAsDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate currentMonthDate = LocalDate.now().minusMonths(i);
                Date endDate = Date.from(currentMonthDate.withDayOfMonth(currentMonthDate.lengthOfMonth())
                        .atStartOfDay(ZoneId.systemDefault()).toInstant());

                // 현재 달 처리
                if (i == 0) {
                    Double closingPrice = coinRepository.findCoinPriceBy(coin.getCurrency());
                    if (closingPrice == null || closingPrice == 0.0) {
                        continue; // closingPrice가 0이거나 null인 경우 계산에서 제외
                    }

                    double purchasePrice = coin.getAvgBuyPrice();
                    if (purchasePrice == 0.0) {
                        continue; // purchasePrice가 0인 경우 계산에서 제외
                    }

                    double coinReturn = ((closingPrice / purchasePrice) * 100) - 100;
                    System.out.println(coin.getCurrency() + "+ Month: " + i + ", closing_price: " + closingPrice + ", Purchase Price: " + purchasePrice + "coinReturn: " + coinReturn);
                    totalCoinReturn += coinReturn;
                    coinCount++;

                    // 과거 달 처리
                } else {
                    if (!purchaseDate.isAfter(currentMonthDate)) {
                        double purchasePrice = coin.getAvgBuyPrice(); // 실제 구매 가격 사용
                        if (purchasePrice == 0.0) {
                            continue; // purchasePrice가 0인 경우 계산에서 제외
                        }

                        Double currentMonthPrice = coinRepository.getCoinPriceForMonth(coin.getCurrency(), endDate,i); // 특정 달의 가격
                        if (currentMonthPrice == null || currentMonthPrice == 0.0) {
                            continue; // currentMonthPrice가 0이거나 null인 경우 계산에서 제외
                        }

                        if (purchasePrice > 0) {
                            double stockReturn = ((currentMonthPrice / purchasePrice) * 100) - 100;
                            System.out.println("Month: " + i + ", Current Month Price: " + currentMonthPrice + ", Purchase Price: " + purchasePrice + ", Stock Return: " + stockReturn);
                            totalCoinReturn += stockReturn;
                            coinCount++;
                        }
                    }
                }
            }

            if (coinCount > 0) {
                double averageCoinReturn = totalCoinReturn / coinCount;
                coinReturns.add(new CoinReturnDTO(i, Double.parseDouble(df.format(averageCoinReturn))));
            } else {
                coinReturns.add(new CoinReturnDTO(i, 0));
            }
        }

        return coinReturns;
    }

    //6개월 채권 수익률
    @Override
    public List<BondReturnDTO> getBondReturnTrend(int uid) {
        List<BondReturnDTO> bondReturns = new ArrayList<>();
        List<Bond> userBonds = bondRepository.findAllByUidAndDeleteDateIsNull(uid);

        for (int i = 0; i < 6; i++) {
            double totalBondReturn = 0;
            int bondCount = 0;

            for (Bond bond : userBonds) {
                Date purchaseDateAsDate = bond.getAddDate();
                LocalDate purchaseDate = purchaseDateAsDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate currentMonthDate = LocalDate.now().minusMonths(i);
                Date endDate = Date.from(currentMonthDate.withDayOfMonth(currentMonthDate.lengthOfMonth())
                        .atStartOfDay(ZoneId.systemDefault()).toInstant());

                if (i == 0) {
                        double currentPrice = 40;
                        double purchasePrice = bond.getPerPrice();
                        System.out.println("Current Bond Price: " + currentPrice + ", Purchase Price: " + purchasePrice);

                        if (purchasePrice > 0) {
                            double bondReturn = ((currentPrice / purchasePrice) * 100) - 100;
                            totalBondReturn += bondReturn;
                            System.out.println("Bond Return: " + bondReturn);
                            bondCount++;
                        }

                } else {
                    if (!purchaseDate.isAfter(currentMonthDate)) {
                        double purchasePrice = bond.getPerPrice();
                        Double currentMonthPrice = bondRepository.getBondPriceForMonth(bond.getItmsNm(), endDate,i);
                        System.out.println("Month: " + i + ", Current Month Price: " + currentMonthPrice + ", Purchase Price: " + purchasePrice);

                        if (currentMonthPrice != null && purchasePrice > 0) {
                            double bondReturn = ((currentMonthPrice / purchasePrice) * 100) - 100;
                            totalBondReturn += bondReturn;
                            System.out.println("Bond Return: " + bondReturn);
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

    private long calculateFinancialAssetsSum(int uid) {
        return sumBankAssets(uid) + sumBondAssets(uid) + sumStockAssets(uid) + sumCoinAssets(uid);
    }


    // 은행 자산 합계
    private long sumBankAssets(int uid) {
        return bankRepository.findAllByUidAndDeleteDateIsNull(uid).stream().mapToLong(Bank::getBalanceAmt).sum();
    }

    // 주식 자산 합계
    private long sumStockAssets(int uid) {
        return stockRepository.getStockBalanceAndPrice(uid).stream()
                .mapToLong(row -> (long) (convertToDouble(row[0]) * convertToDouble(row[1]))).sum();
    }

    // 코인 자산 합계
    private long sumCoinAssets(int uid) {
        return coinRepository.findCoinBalanceAndPriceByUid(uid).stream()
                .mapToLong(row -> (long) (convertToDouble(row[0]) * convertToDouble(row[1]))).sum();
    }

    // 채권 자산 합계
    private long sumBondAssets(int uid) {
        return bondRepository.findAllByUidAndDeleteDateIsNull(uid).stream()
                .mapToLong(bond -> (long) bond.getPerPrice() * bond.getCnt()).sum();
    }

    // Spot 자산 합산 메서드
    private long calculateSpotAssetsSum(int uid) {
        List<Spot> spotData = spotRepository.findAllByUidAndDeleteDateIsNull(User.builder().uid(uid).build());
        return spotData.stream().mapToLong(Spot::getPrice).sum();
    }

    // 월별 금융 자산 합산 메서드
    private long calculateMonthlyAssetsSum(int uid, int monthsAgo) {


        LocalDateTime endOfMonthLDT = LocalDate.now().minusMonths(monthsAgo).withDayOfMonth(1).atStartOfDay();
        Date endOfMonth = Date.from(endOfMonthLDT.atZone(ZoneId.systemDefault()).toInstant());

        long monthlySum = 0;

        // 은행 자산 합산
        List<Bank> bankAssets = bankRepository.findAllByUidAndAddDateBefore(uid, endOfMonth);
        monthlySum += bankAssets.stream().mapToLong(Bank::getBalanceAmt).sum();

        // 채권 자산 합산
        List<Bond> bondAssets = bondRepository.findAllByUidAndAddDateBefore(uid, endOfMonth,monthsAgo);
        monthlySum += bondAssets.stream().mapToLong(bond -> (long) bond.getPerPrice() * bond.getCnt()).sum();

        // 가상화폐 자산 합산
        List<Object[]> coinData = coinRepository.findCoinBalanceAndPriceByUserIdAndBefore(uid, endOfMonth, monthsAgo);
        monthlySum += coinData.stream()
                .mapToLong(row -> (long) ((double) row[0] * Integer.parseInt((String) row[1])))
                .sum();

        // 주식 자산 합산
        List<Object[]> stockData = stockRepository.getStockBalanceAndClosingPriceBefore(uid, endOfMonth, monthsAgo);
        monthlySum += stockData.stream()
                .mapToLong(row -> (long) ((double) row[0] * (double) row[1]))
                .sum();

        return monthlySum;
    }

    // Spot 자산 월별 합산 메서드
    private long calculateSpotAssetsSumBefore(int uid, int monthsAgo) {
        User user = User.builder().uid(uid).build();
        LocalDateTime endOfMonthLDT = LocalDate.now().minusMonths(monthsAgo).withDayOfMonth(1).atStartOfDay();
        Date endOfMonth = Date.from(endOfMonthLDT.atZone(ZoneId.systemDefault()).toInstant());
        List<Spot> spotAssets = spotRepository.findAllByUidAndAddDateBefore(user, endOfMonth);
        return spotAssets.stream().mapToLong(Spot::getPrice).sum();
    }

}