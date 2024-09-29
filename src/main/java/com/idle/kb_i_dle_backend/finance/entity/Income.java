package com.idle.kb_i_dle_backend.finance.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="income" , catalog="asset")
public class Income implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int index;

    @Column(name = "uid")
    private int uid;

    @Column(name = "type")
    private String type;

    @Column(name = "amount")
    private long amount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    private Date date;

    @Column(name = "descript")
    private String descript;

    @Column(name = "memo")
    private String memo;

}
