package com.idle.kb_i_dle_backend.domain.finance.dto;

import com.idle.kb_i_dle_backend.domain.finance.entity.Bank;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.global.DateUtil;
import lombok.*;


import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
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

        Integer index = bank.getIndex();
        String orgCode = bank.getOrgCode();
        String accountNum = bank.getAccountNum();
        String name = bank.getName();
        String category = bank.getCategory();
        String accountType = bank.getAccountType();
        Long balanceAmt = bank.getBalanceAmt();
        String addDate = (bank.getAddDate() != null) ? dateFormat.format(bank.getAddDate()) : null;
        String deleteDate = (bank.getDeleteDate() != null) ? dateFormat.format(bank.getDeleteDate()) : null;

        return new BankDTO(index, orgCode, accountNum, name, category, accountType, balanceAmt, addDate, deleteDate);
    }

    public static Bank convertToEntity(Member member, BankDTO bankDTO) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Date addDate = DateUtil.parseDateToUtilDate(bankDTO.getAddDate(), formatter);
        Date delDate = DateUtil.parseDateToUtilDate(bankDTO.getDeleteDate(), formatter);

        String type = switch (bankDTO.getProdCategory()) {
            case "예금" -> "01";
            case "적금" -> "02";
            case "청약" -> "03";
            case "입출금" -> "04";
            default -> "00";
        };

        return new Bank(
                bankDTO.getIndex(),
                member,
                bankDTO.getOrgCode(),
                bankDTO.getAccountNum(),
                bankDTO.getProdName(),
                bankDTO.getProdCategory(),
                type,
                "KRW",
                bankDTO.getBalanceAmt(),
                addDate,
                delDate
        );
    }
}
