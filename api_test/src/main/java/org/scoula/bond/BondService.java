package org.scoula.bond;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BondService {
    private final BondMapper bondMapper;

    @Autowired
    public BondService(BondMapper bondMapper) {
        this.bondMapper = bondMapper;
    }

    public void saveBond(BondVO bondvo) {
        bondMapper.insertBond(bondvo);
    }
}
