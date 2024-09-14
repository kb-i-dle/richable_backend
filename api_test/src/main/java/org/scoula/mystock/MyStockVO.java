package org.scoula.mystock;

import lombok.Data;

@Data
public class MyStockVO {
    private int index;
    private String standardCode;
    private String shortCode;
    private String koreanStockName;
    private String koreanStockAbbr;
    private String market;
    private double price;
}
