package com.idle.kb_i_dle_backend.domain.finance.dto;

import com.idle.kb_i_dle_backend.domain.finance.entity.Coin;
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
public class CoinDTO {
    private Integer index;
    private String currency;
    private Double balance;
    private Double avgBuyPrice;
    private String addDate;
    private String deleteDate;

    public static CoinDTO convertToDTO(Coin coin) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return new CoinDTO(coin.getIndex(), coin.getCurrency(), coin.getBalance(), coin.getAvgBuyPrice(), (coin.getAddDate() != null) ? dateFormat.format(coin.getAddDate()) : null, (coin.getDeleteDate() != null) ? dateFormat.format(coin.getDeleteDate()) : null);
    }

    public static Coin convertToEntity(Member member, CoinDTO coinDTO) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date addDate = (coinDTO.getAddDate() != null)
                ? dateFormat.parse(coinDTO.getAddDate())
                : null;  // null 값 유지
        Date delDate = (coinDTO.getDeleteDate() != null)
                ? dateFormat.parse(coinDTO.getDeleteDate())
                : null;  // null 값 유지
        return new Coin(coinDTO.getIndex(), member, coinDTO.getCurrency(), coinDTO.getBalance(), coinDTO.getAvgBuyPrice(), "KRW", "coin" , addDate, delDate);
    }
}
