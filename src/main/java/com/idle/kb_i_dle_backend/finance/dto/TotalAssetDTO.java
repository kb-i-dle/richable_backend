package com.idle.kb_i_dle_backend.finance.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalAssetDTO {
    private long bsAmount;  // 금융 + 현물 자산 합계

    public TotalAssetDTO(long bsAmount) {
        this.bsAmount = bsAmount;
    }
}
