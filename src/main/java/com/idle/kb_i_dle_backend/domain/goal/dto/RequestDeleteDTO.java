package com.idle.kb_i_dle_backend.domain.goal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDeleteDTO {
    private int index;
    private String category;

    public RequestDeleteDTO(int index, String category) {
        this.index = index;
        this.category = category;
    }
}
