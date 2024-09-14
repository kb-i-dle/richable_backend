package org.scoula.mystock;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j
@Service
public class MyStockService {

    private static MyStockMapper stockMapper;

    @Autowired
    public MyStockService(MyStockMapper stockMapper) {
        this.stockMapper = stockMapper;
    }

    public List<MyStockVO> getAllStocks() {
        return stockMapper.getAllStocks();
    }

    public void updateStockPrice(String shortCode, double price) {
        stockMapper.updatePriceByShortCode(price, shortCode);
    }

    public static void getStockByShortCode(String shortCode, double price) {
        stockMapper.getStockByShortCode(shortCode);
    }
}
