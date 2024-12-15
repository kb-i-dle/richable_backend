package com.idle.kb_i_dle_backend.domain.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResponseUpdateAchiveDTO {
    private int index;
    private boolean isAchive;
    private int priority;

    public boolean getIsAchive() {
        return isAchive;
    }
}
