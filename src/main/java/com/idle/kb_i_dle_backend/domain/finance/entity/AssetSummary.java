package com.idle.kb_i_dle_backend.domain.finance.entity;

import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import java.math.BigDecimal;
import lombok.Getter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "asset_summary", catalog = "asset")
@Getter
public class AssetSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer index;

    @ManyToOne
    @JoinColumn(name = "uid")
    private Member uid;

    private Long bond;
    private Long deposit;
    private Long saving;
    private Long subscription;
    private Long withdrawal;
    private Long cash;
    private Long stock;
    private Long coin;

    @Column(name = "total_amount")
    private Long totalAmount;

    @Column(name = "update_date")
    private Date updateDate;

    // 엔티티가 처음 영속화될 때(Date를 자동으로 설정)
    @PrePersist
    protected void onCreate() {
        this.updateDate = new Date();  // 현재 시간을 자동으로 설정
    }
}
