package com.idle.kb_i_dle_backend.domain.finance.dto;

import com.idle.kb_i_dle_backend.domain.finance.entity.Bank;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import lombok.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BankDTO {
    private Integer index;
    private String orgCode;
    private String accountNum;
    private String prodName;
    private String prodCategory;
    private String accountType;
    private Long balanceAmt;
    private String addDate;
    private String deleteDate;

    public static BankDTO convertToDTO(Bank bank) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        return new BankDTO(bank.getIndex(), bank.getOrgCode(), bank.getAccountNum(), bank.getName(), bank.getCategory(), bank.getAccountType(), bank.getBalanceAmt(), (bank.getAddDate() != null) ? dateFormat.format(bank.getAddDate()) : null, (bank.getDeleteDate() != null) ? dateFormat.format(bank.getDeleteDate()) : null);
    }

    public static Bank convertToEntity(Member member, BankDTO bankDTO) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date addDate = (bankDTO.getAddDate() != null)
                ? dateFormat.parse(bankDTO.getAddDate())
                : null;  // null 값 유지
        Date delDate = (bankDTO.getDeleteDate() != null)
                ? dateFormat.parse(bankDTO.getDeleteDate())
                : null;  // null 값 유지
        String type = bankDTO.getProdCategory().equals("예금") ? "01" :
                        bankDTO.getProdCategory().equals("적금") ? "02" :
                        bankDTO.getProdCategory().equals("청약") ? "03" :
                        bankDTO.getProdCategory().equals("입출금") ? "04" : "00";

        return new Bank(bankDTO.getIndex(), member, bankDTO.getOrgCode(), bankDTO.getAccountNum(), bankDTO.getProdName(), bankDTO.getProdCategory(), type, "KRW", bankDTO.getBalanceAmt(), addDate, delDate);
    }
}
