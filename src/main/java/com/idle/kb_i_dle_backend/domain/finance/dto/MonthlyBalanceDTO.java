package com.idle.kb_i_dle_backend.domain.finance.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class MonthlyBalanceDTO {
    private String month;
    private Long totalIncome;
    private Long totalOutcome;
    private Long balance;
}
