package com.idle.kb_i_dle_backend.finance.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthlySavingRateDTO {
    private int month;          // 월
    private double restBalance;  // 저축 금액

    public MonthlySavingRateDTO(int month, double restBalance) {
        this.month = month;
        this.restBalance = restBalance;
    }
}
