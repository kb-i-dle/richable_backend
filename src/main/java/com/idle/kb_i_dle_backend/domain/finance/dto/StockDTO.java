package com.idle.kb_i_dle_backend.domain.finance.dto;

import com.idle.kb_i_dle_backend.domain.finance.entity.Stock;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.global.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

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

    public static Stock convertToEntity(Member member, StockDTO stockDTO) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Date addDate = DateUtil.parseDateToUtilDate(stockDTO.getAddDate(), formatter);
        Date delDate = DateUtil.parseDateToUtilDate(stockDTO.getDeleteDate(), formatter);

        return new Stock(
                stockDTO.getIndex(),
                member,
                stockDTO.getPdno(),
                stockDTO.getPrdtName(),
                stockDTO.getHldgQty(),
                "stock",
                stockDTO.getAvgBuyPrice(),
                addDate,
                delDate
        );
    }
}
