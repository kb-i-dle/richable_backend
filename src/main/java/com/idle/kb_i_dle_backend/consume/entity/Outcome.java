package com.idle.kb_i_dle_backend.consume.entity;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Table(name = "outcome_average", catalog = "outcome") // Ensure this table exists or modify the name to match your DB structure
public class Outcome {


    @Id
    @Column(name = "outcome_expenditure_category")
    private String outcomeExp;

    // outcome_average fields
    @Column(name = "household_head_age_group")
    private String householdHeadAgeGroup;  // Age group of the household head

    @Column(name = "outcome")
    private Integer outcome;  // Outcome amount from the average table

    @Column(name = "household_size", precision =3, scale = 2)
    private BigDecimal householdSize;  // Size of the household

    @Column(name = "quater")
    private String quater;  // Quarter information

}
