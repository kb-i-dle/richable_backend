package com.idle.kb_i_dle_backend.domain.outcome.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "outcome_expenditure_category", catalog = "outcome")
@Getter
public class OutcomeCategory {

    @Id
    private Integer index;

    @Column(name = "category_name")
    private String categoryName;

    public void setCategoryName(String category) {
        this.categoryName = category;
    }
}
