package com.idle.kb_i_dle_backend.finance.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BondReturnDTO {


        private int month;          // 월
        private double earningRate;  // 가상화폐 수익률

        public BondReturnDTO(int month, double earningRate) {
            this.month = month;
            this.earningRate = earningRate;
        }

}
