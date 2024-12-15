package com.idle.kb_i_dle_backend.domain.finance.dto;

import com.idle.kb_i_dle_backend.domain.finance.entity.Spot;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpotDTO {
    private Integer index;
    private String category;
    private String name;
    private Long price;
    private String addDate;
    private String deleteDate;

    public static SpotDTO convertToDTO(Spot spot) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Integer index = spot.getIndex();
        String category = spot.getCategory();
        String name = spot.getName();
        Long price = spot.getPrice();
        String addDate = (spot.getAddDate() != null) ? dateFormat.format(spot.getAddDate()) : null;
        String deleteDate = (spot.getDeleteDate() != null) ? dateFormat.format(spot.getDeleteDate()) : null;

        return new SpotDTO(index, category, name, price, addDate, deleteDate);
    }

    public static Spot convertToEntity(Member member, SpotDTO spotDTO) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date addDate = (spotDTO.getAddDate() != null)
                ? dateFormat.parse(spotDTO.getAddDate())
                : null;  // null 값 유지
        Date delDate = (spotDTO.getDeleteDate() != null)
                ? dateFormat.parse(spotDTO.getDeleteDate())
                : null;  // null 값 유지
        return new Spot(spotDTO.getIndex(), member, spotDTO.getCategory(), spotDTO.getName(), spotDTO.getPrice(), "spot", addDate, delDate);
    }
}
