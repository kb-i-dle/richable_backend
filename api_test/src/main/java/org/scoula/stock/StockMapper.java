package org.scoula.stock;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface StockMapper {
    List<StockInfo> getAllStockInfo();
}
