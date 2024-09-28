
package com.idle.kb_i_dle_backend.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//uid를 필드 추가함
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserApiDTO {
    private Integer uid;
    private String apiBank;
    private String apiStock;
    private String apiStockToken;
    private String apiStockSecret;
    private String apiStockApp;
    private String apiCoin;
    private String apiCoinApp;
    private String apiCoinSecret;
}

