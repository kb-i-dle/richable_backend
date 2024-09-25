package com.idle.kb_i_dle_backend.finance.entity;

import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="spot" , catalog="asset")
public class Spot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int index;

    private int uid;

    private String category;

    private String name;

    private long price;

    private String prod_category;

    private int add_date;

    private int delete_date;
}
