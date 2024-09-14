package org.scoula.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StockService {
    private final StockMapper stockMapper;
    private final JdbcTemplate jdbcTemplate;

    // Constructor for dependency injection
    @Autowired
    public StockService(StockMapper stockMapper, @Qualifier("productJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.stockMapper = stockMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    // Method to get all stock information using StockMapper (MyBatis)
    public List<StockVO> getAllStocks() {
        return stockMapper.getAllStockInfo();
    }

    public void updateStockData(String price, String standardCode) {
        StockVO StockVO = new StockVO();
        StockVO.setStandardCode(standardCode);
        StockVO.setPrice(price);
        stockMapper.updateStockPrice(StockVO);
    }

}
