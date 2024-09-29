package com.idle.kb_i_dle_backend.finance.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalChangeDTO {
    private int month;        // 월
    private long balance;     // 해당 월의 금융 자산 + 현물 자산 잔액

    public TotalChangeDTO(int month, long balance) {
        this.month = month;
        this.balance = balance;
    }
}
