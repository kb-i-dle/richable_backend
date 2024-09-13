package org.scoula.stock;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StockService {
    private final StockMapper stockMapper;

    public StockService(StockMapper stockMapper) {
        this.stockMapper = stockMapper;
    }

    public List<StockInfo> getAllStocks() {
        return stockMapper.getAllStockInfo();
    }
}
