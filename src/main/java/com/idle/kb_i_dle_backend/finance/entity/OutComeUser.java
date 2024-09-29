package com.idle.kb_i_dle_backend.finance.entity;

import java.util.Date;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "outcome_user", catalog = "outcome")
public class OutComeUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "index")
    private int index;

    @Column(name = "uid", nullable = false)
    private int uid;

    @Column(name = "outcome_expenditure_category", length = 100, nullable = false)
    private String category;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Column(name = "descript", length = 255)
    private String descript;

    @Column(name = "memo", length = 255)
    private String memo;

}
