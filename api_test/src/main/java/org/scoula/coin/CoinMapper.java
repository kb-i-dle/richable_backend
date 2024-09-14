package org.scoula.coin;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CoinMapper {
//    @Insert("INSERT INTO coin_prices (coin_name, closing_price) VALUES (#{coinName}, #{closingPrice})")
    void insertCoinPrice(CoinVO coinVO);
    void updateCoinPrice(CoinVO coinVO);

}
