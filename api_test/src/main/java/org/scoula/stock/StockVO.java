package org.scoula.stock;

import lombok.Data;

@Data
public class StockVO {
    private int index;
    private String standardCode;
    private String shortCode;
    private String koreanStockName;
    private String koreanStockAbbr;
    private String market;
    private String price;
}
