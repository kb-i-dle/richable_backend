package com.idle.kb_i_dle_backend.finance.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockReturnDTO {
    private int month;          // 월
    private double earningRate;  // 주식 수익률

    public StockReturnDTO(int month, double earningRate) {
        this.month = month;
        this.earningRate = earningRate;
    }
}
