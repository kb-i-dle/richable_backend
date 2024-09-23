package com.idle.kb_i_dle_backend.consume.entity;

import lombok.Getter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Table(name = "outcome_user", catalog = "outcome") // Ensure this table exists or modify the name to match your DB structure
public class OutcomeUser {

    // outcome_user fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int index;

    private int uid;

    @Column(name = "outcome_expenditure_category")
    private String category;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private long amount;

    private String descript;

    private String memo;

}
