package com.idle.kb_i_dle_backend.domain.finance.dto;

import com.idle.kb_i_dle_backend.domain.finance.entity.Coin;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.global.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CoinDTO {
    private Integer index;
    private String currency;
    private Double balance;
    private Double avgBuyPrice;
    private String addDate;
    private String deleteDate;

    public static CoinDTO convertToDTO(Coin coin) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Integer index = coin.getIndex();
        String currency = coin.getCurrency();
        Double balance = coin.getBalance();
        Double avgBuyPrice = coin.getAvgBuyPrice();
        String addDate = (coin.getAddDate() != null) ? dateFormat.format(coin.getAddDate()) : null;
        String deleteDate = (coin.getDeleteDate() != null) ? dateFormat.format(coin.getDeleteDate()) : null;

        return new CoinDTO(index, currency, balance, avgBuyPrice, addDate, deleteDate);
    }

    public static Coin convertToEntity(Member member, CoinDTO coinDTO) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Date addDate = DateUtil.parseDateToUtilDate(coinDTO.getAddDate(), formatter);
        Date delDate = DateUtil.parseDateToUtilDate(coinDTO.getDeleteDate(), formatter);

        return new Coin(
                coinDTO.getIndex(),
                member,
                coinDTO.getCurrency(),
                coinDTO.getBalance(),
                coinDTO.getAvgBuyPrice(),
                "KRW",
                "coin",
                addDate,
                delDate
        );
    }
}
