package org.scoula.mystock;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Log4j
@Service
public class MyStockService {

    @Autowired
    private static MyStockMapper myStockMapper;

    @Autowired
    public MyStockService(MyStockMapper myStockMapper) {
        this.myStockMapper = myStockMapper;
    }

    public static void updatePriceByShortCode(String stockCode, int price) {
        log.info("Updating stockCode: {} with price: {}");
        myStockMapper.updatePriceByShortCode(stockCode, price);
    }

    // 기존의 종목들을 불러오는 메서드 (이미 구현된 부분)
    public List<MyStockInfo> getAllStocks() {
        return myStockMapper.getAllStocks();
    }

}
