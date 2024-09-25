package com.idle.kb_i_dle_backend.finance.entity;

import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="stock_list_price" , catalog="product")
public class Stock_list_price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int index;

    private String standard_code;

    private int date;

    private int oneMonthAgoPrice;

    private int twoMonthsAgoPrice;

    private int threeMonthsAgoPrice;

    private int fourMonthsAgoPrice;

    private int fiveMonthsAgoPrice;

    private int sixMonthsAgoPrice;
}
