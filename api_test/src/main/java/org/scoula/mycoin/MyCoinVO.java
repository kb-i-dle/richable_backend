package org.scoula.mycoin;

import lombok.Data;

@Data
public class MyCoinVO {
    private int uid;
    private double balance;
    private double avgBuyPrice;
    private String unitCurrency;
    private String currency;
}
