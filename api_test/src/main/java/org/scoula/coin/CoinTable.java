package org.scoula.coin;

import lombok.Data;

@Data
public class CoinTable {
    private String coinName;
    private String closingPrice;

    // Constructor for initializing the object
    public CoinTable(String coinName, String closingPrice) {
        this.coinName = coinName;
        this.closingPrice = closingPrice;
    }
}
