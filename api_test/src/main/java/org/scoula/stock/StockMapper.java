package org.scoula.stock;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.stock.StockVO;
import java.util.List;

@Mapper
public interface StockMapper {
    List<StockVO> getAllStockInfo();  // Get all stock info

    void updateStockPrice(StockVO Stockvo);  // Update stock price based on standardCode
}