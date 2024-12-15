package com.idle.kb_i_dle_backend.domain.outcome.dto;

import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.outcome.entity.OutcomeUser;
import java.math.BigInteger;
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
public class OutcomeUserDTO {
    private Integer index;
    private String expCategory;
    private String date;
    private Long amount;
    private String descript;
    private String memo;
    private Long accountNum;

    public static OutcomeUserDTO convertToDTO(OutcomeUser outcomeUser) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return new OutcomeUserDTO(outcomeUser.getIndex(), outcomeUser.getCategory(), dateFormat.format(outcomeUser.getDate()), outcomeUser.getAmount(), outcomeUser.getDescript(), outcomeUser.getMemo(), outcomeUser.getAccountNum());
    }

    public static OutcomeUser convertToEntity(Member member, OutcomeUserDTO outcomeUserDTO) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date oDate = (outcomeUserDTO.getDate() != null)
                ? dateFormat.parse(outcomeUserDTO.getDate())
                : null;  // null 값 유지
        return new OutcomeUser(outcomeUserDTO.getIndex(), member, outcomeUserDTO.getExpCategory(), oDate, outcomeUserDTO.getAmount(), outcomeUserDTO.getDescript(), outcomeUserDTO.getMemo(), outcomeUserDTO.getAccountNum());
    }

}
