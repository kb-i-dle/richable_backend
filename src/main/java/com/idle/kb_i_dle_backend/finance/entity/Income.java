package com.idle.kb_i_dle_backend.finance.entity;

import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="income" , catalog="asset")
public class Income {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int index;

    private int uid;

    private String type;

    private long amount;

    private int date;

    private String descript;

    private String memo;

}
