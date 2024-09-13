package org.scoula.mystock;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MyStockMapper {

    void updatePriceByShortCode(@Param("stockCode") String stockCode, @Param("price") int price);

    List<MyStockInfo> getAllStocks();
}
