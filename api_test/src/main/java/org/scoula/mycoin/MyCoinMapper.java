package org.scoula.mycoin;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface  MyCoinMapper {
    // Insert or update coin data
    void insertOrUpdateCoin(MyCoinVO coin);
}
