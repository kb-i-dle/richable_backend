package com.idle.kb_i_dle_backend.finance.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpotAssetDTO {
    private String prodCategory;  // 자산 카테고리 (예금, 적금 등)
    private long bAmount;         // 금융 자산 금액
    private String prodSpot;      // 현물 자산 이름 (예: 에어팟)
    private long bsAmount;        // 현물 자산 금액

    public SpotAssetDTO(String prodCategory, long bAmount, String prodSpot, long bsAmount) {
        this.prodCategory = prodCategory;
        this.bAmount = bAmount;
        this.prodSpot = prodSpot;
        this.bsAmount = bsAmount;
    }
}
