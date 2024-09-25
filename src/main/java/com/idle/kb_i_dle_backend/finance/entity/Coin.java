package com.idle.kb_i_dle_backend.finance.entity;

import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="coin" , catalog="asset")
public class Coin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int index;

    private int uid;

    private String currency;

    private double balance;

    private double avg_buy_price;

    private String unit_currency;

    private String prod_category;

    private int add_date;

    private int delete_date;
}
