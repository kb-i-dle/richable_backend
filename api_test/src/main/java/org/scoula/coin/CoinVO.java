package org.scoula.coin;

import lombok.Data;

@Data
public class CoinVO {
    private String coinName;
    private String closingPrice;

    // Constructor for initializing the object
    public CoinVO(String coinName, String closingPrice) {
        this.coinName = coinName;
        this.closingPrice = closingPrice;
    }
}
