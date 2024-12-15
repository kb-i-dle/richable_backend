package com.idle.kb_i_dle_backend.domain.goal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestIndexDTO {
    private int index;

    public RequestIndexDTO(int index) {
        this.index = index;
    }
}
