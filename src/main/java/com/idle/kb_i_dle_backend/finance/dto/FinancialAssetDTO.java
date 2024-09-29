package com.idle.kb_i_dle_backend.finance.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinancialAssetDTO {
    private String prodCategory;  // 예금, 적금 등의 카테고리
    private long bAmount;         // 각 금융 자산의 금액

    public FinancialAssetDTO(String prodCategory, long bAmount) {
        this.prodCategory = prodCategory;
        this.bAmount = bAmount;
    }
}
