package com.idle.kb_i_dle_backend.domain.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceSumDTO {
    private String prodCategory;
    private Long amount;
}
