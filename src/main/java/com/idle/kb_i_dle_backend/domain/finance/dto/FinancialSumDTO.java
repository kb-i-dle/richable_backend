package com.idle.kb_i_dle_backend.domain.finance.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinancialSumDTO {
    private Long amount;  // 금융 자산 합계

    public FinancialSumDTO(Long amount) {
        this.amount = amount;
    }
}
