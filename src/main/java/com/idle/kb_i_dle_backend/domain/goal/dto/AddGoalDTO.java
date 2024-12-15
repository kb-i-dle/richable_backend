package com.idle.kb_i_dle_backend.domain.goal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddGoalDTO {
    private String category;
    private String title;
    private long amount;

    public AddGoalDTO(String category, String title, long amount) {
        this.category = category;
        this.title = title;
        this.amount = amount;
    }
}
