package org.scoula.coin;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CoinMapper {
//    @Insert("INSERT INTO coin_prices (coin_name, closing_price) VALUES (#{coinName}, #{closingPrice})")
    void insertCoinPrice(CoinTable coinTable);
    void updateCoinPrice(CoinTable coinTable);

}
