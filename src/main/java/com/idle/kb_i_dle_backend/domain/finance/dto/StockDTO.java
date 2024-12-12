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

        Integer index = stock.getIndex();
        Integer pdno = stock.getPdno();
        String prdtName = stock.getPrdtName();
        Integer hldgQty = stock.getHldgQty();
        Integer avgBuyPrice = stock.getAvgBuyPrice();
        String addDate = (stock.getAddDate() != null) ? dateFormat.format(stock.getAddDate()) : null;
        String deleteDate = (stock.getDeleteDate() != null) ? dateFormat.format(stock.getDeleteDate()) : null;

        return new StockDTO(index, pdno, prdtName, hldgQty, avgBuyPrice, addDate, deleteDate);
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
