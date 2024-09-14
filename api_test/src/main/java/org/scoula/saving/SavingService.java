package org.scoula.saving;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SavingService {
    private final SavingMapper SavingMapper;

    @Autowired
    public SavingService(SavingMapper SavingMapper) {
        this.SavingMapper = SavingMapper;
    }

    public void saveSavingProduct(SavingVO product) {
        SavingMapper.insertSavingProduct(product);
    }
}
