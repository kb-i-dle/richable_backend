package org.scoula.bond;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BondMapper {
    void insertBond(BondVO bondvo);
}
