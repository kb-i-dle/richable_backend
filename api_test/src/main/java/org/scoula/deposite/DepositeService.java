package org.scoula.deposite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DepositeService {
    private final DepositeMapper DepositeMapper;

    @Autowired
    public DepositeService(DepositeMapper DepositeMapper) {
        this.DepositeMapper = DepositeMapper;
    }

    public void saveDepositeProduct(DepositeVO product) {
        DepositeMapper.insertDepositeProduct(product);
    }
}
