package com.idle.kb_i_dle_backend.domain.finance.entity;

import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bond", catalog = "asset")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Bond {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`index`")
    private Integer index;

    @ManyToOne
    @JoinColumn(name = "uid")
    private Member uid;

    @NotNull
    @Column(name = "itms_nm")
    private String  name;

    @NotNull
    private Integer cnt;

    @Column(name = "prod_category", length = 100)
    private String category;

    @Column(name = "per_price")
    private Integer price;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "add_date")
    private Date addDate;

    @Column(name = "delete_date")
    private Date deleteDate;

    // 엔티티가 처음 영속화될 때(addDate를 자동으로 설정)
    @PrePersist
    protected void onCreate() {
        this.addDate = new Date();  // 현재 시간을 자동으로 설정
    }

}
