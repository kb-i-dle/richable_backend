package com.idle.kb_i_dle_backend.finance.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetDTO {
    private String prodCategory;
    private long amount;

    public AssetDTO(String prodCategory, long amount) {
        this.prodCategory = prodCategory;
        this.amount = amount;
    }

}
