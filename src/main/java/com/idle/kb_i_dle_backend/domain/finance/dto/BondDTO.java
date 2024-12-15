package com.idle.kb_i_dle_backend.domain.finance.dto;

import com.idle.kb_i_dle_backend.domain.finance.entity.Bond;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.global.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BondDTO {
    private Integer index;
    private String name;
    private Integer cnt;
    private Integer price;
    private String addDate;
    private String deleteDate;

    public static BondDTO convertToDTO(Bond bond) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Integer index = bond.getIndex();
        String name = bond.getName();
        Integer count = bond.getCnt();
        Integer price = bond.getPrice();
        String addDate = (bond.getAddDate() != null) ? dateFormat.format(bond.getAddDate()) : null;
        String deleteDate = (bond.getDeleteDate() != null) ? dateFormat.format(bond.getDeleteDate()) : null;

        return new BondDTO(index, name, count, price, addDate, deleteDate);
    }

    public static Bond convertToEntity(Member member, BondDTO bondDTO) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Date addDate = DateUtil.parseDateToUtilDate(bondDTO.getAddDate(), formatter);
        Date delDate = DateUtil.parseDateToUtilDate(bondDTO.getDeleteDate(), formatter);

        return new Bond(
                bondDTO.getIndex(),
                member,
                bondDTO.getName(),
                bondDTO.getCnt(),
                "bond",
                bondDTO.getPrice(),
                addDate,
                delDate
        );
    }
}
