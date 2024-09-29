package com.idle.kb_i_dle_backend.finance.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinancialChangeDTO {
    private int month;        // 월
    private long balance;     // 해당 월의 금융 자산 잔액

    public FinancialChangeDTO(int month, long balance) {
        this.month = month;
        this.balance = balance;
    }
}
