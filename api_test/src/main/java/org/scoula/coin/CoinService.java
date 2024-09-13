package org.scoula.coin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class CoinService {

    private final CoinMapper coinMapper;

    public CoinService(CoinMapper coinMapper) {
        this.coinMapper = coinMapper;
    }

    @Transactional
    public void saveCoinPrices(Map<String, String> closingPrices) {
        // Loop through each coin and save the prices
        for (Map.Entry<String, String> entry : closingPrices.entrySet()) {
            CoinTable coinTable = new CoinTable(entry.getKey(), entry.getValue());
            coinMapper.insertCoinPrice(coinTable);
        }
    }
    @Transactional
    public void updateCoinPrices(Map<String, String> closingPrices) {
        for (Map.Entry<String, String> entry : closingPrices.entrySet()) {
            CoinTable coinTable = new CoinTable(entry.getKey(), entry.getValue());
            coinMapper.updateCoinPrice(coinTable);
        }
    }
}
