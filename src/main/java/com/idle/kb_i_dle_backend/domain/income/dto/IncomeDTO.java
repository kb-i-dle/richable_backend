package com.idle.kb_i_dle_backend.domain.income.dto;

import com.idle.kb_i_dle_backend.domain.income.entity.Income;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import java.math.BigInteger;
import lombok.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IncomeDTO {
    private Integer incomeId;
    private String type;
    private String incomeDate;
    private Long price;
    private String contents;
    private String memo;
    private Long accountNum;

    public static IncomeDTO convertToDTO(Income income) {
        if (income == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return new IncomeDTO(income.getIndex(), income.getType(), dateFormat.format(income.getDate()), income.getAmount(), income.getDescript(), income.getMemo(), income.getAccountNum());
    }

    public static Income convertToEntity(Member member, IncomeDTO incomeDTO) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date iDate = (incomeDTO.getIncomeDate() != null)
                ? dateFormat.parse(incomeDTO.getIncomeDate())
                : null;  // null 값 유지
        return new Income(incomeDTO.getIncomeId(), member, incomeDTO.getType(), incomeDTO.getPrice(), iDate, incomeDTO.getContents(), incomeDTO.getMemo(), incomeDTO.getAccountNum());
    }
}
