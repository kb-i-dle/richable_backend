package com.idle.kb_i_dle_backend.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssetSpotDTO {
    String uid;
    String category;
    String name;
    String price;
}
