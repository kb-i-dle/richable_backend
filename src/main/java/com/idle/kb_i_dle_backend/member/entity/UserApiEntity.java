package com.idle.kb_i_dle_backend.member.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
//Userapientity 함
@Entity
@Getter
@Table(name = "user_api", catalog = "user_info")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserApiEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer uid;

    private String api_bank;
    private String api_stock;
    private String api_stock_token;
    private String api_stock_secret;
    private String api_stock_app;
    private String api_coin;
    private String api_coin_app;
    private String api_coin_secret;

    @OneToOne
    @MapsId  // User와 uid 필드를 매핑
    @JoinColumn(name = "uid")
    private User user;
}
