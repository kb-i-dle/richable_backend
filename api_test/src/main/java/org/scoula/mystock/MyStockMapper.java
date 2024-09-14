package org.scoula.mystock;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MyStockMapper {

    // 전체 종목 목록 조회
    List<MyStockVO> getAllStocks();

    // 단축코드로 가격 업데이트
    void updatePriceByShortCode(@Param("price") double price, @Param("stockCode") String stockCode);

    // 단축코드로 종목 정보 조회
    MyStockVO getStockByShortCode(@Param("shortCode") String shortCode);
}
