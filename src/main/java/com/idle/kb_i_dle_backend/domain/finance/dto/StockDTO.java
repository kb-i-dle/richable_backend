package com.idle.kb_i_dle_backend.domain.finance.dto;

import com.idle.kb_i_dle_backend.domain.finance.entity.Coin;
import com.idle.kb_i_dle_backend.domain.finance.entity.Stock;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockDTO {
    private Integer index;
    private Integer pdno;
    private String prdtName;
    private Integer hldgQty;
    private Integer avgBuyPrice;
    private String addDate;
    private String deleteDate;

    public static StockDTO convertToDTO(Stock stock) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return new StockDTO(stock.getIndex(), stock.getPdno(), stock.getPrdtName(), stock.getHldgQty(), stock.getAvgBuyPrice(), (stock.getAddDate() != null) ? dateFormat.format(stock.getAddDate()) : null, (stock.getDeleteDate() != null) ? dateFormat.format(stock.getDeleteDate()) : null);
    }

    public static Stock convertToEntity(Member member, StockDTO stockDTO) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date addDate = (stockDTO.getAddDate() != null)
                ? dateFormat.parse(stockDTO.getAddDate())
                : null;  // null 값 유지
        Date delDate = (stockDTO.getDeleteDate() != null)
                ? dateFormat.parse(stockDTO.getDeleteDate())
                : null;  // null 값 유지
        return new Stock(stockDTO.getIndex(), member, stockDTO.getPdno(), stockDTO.getPrdtName(), stockDTO.getHldgQty(), "stock", stockDTO.getAvgBuyPrice(), addDate, delDate);
    }
}
