package com.idle.kb_i_dle_backend.domain.goal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestPriorityDTO {
    private int index;
    private int priority;

    public RequestPriorityDTO(int index, int priority) {
        this.index = index;
        this.priority = priority;
    }
}
