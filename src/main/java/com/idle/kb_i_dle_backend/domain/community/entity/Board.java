package com.idle.kb_i_dle_backend.domain.community.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import lombok.Getter;

import java.util.Date;

@Entity
@Getter
@Table(name = "board_gallery", catalog = "board") // table 설정, 스키마 설정 table이름이나 컬럼은 DB와 같다면 따로 지정 안 해도 되긴 함
public class Board {
    /**
     * Entity는 id값이 필수로 있어야 한다.
     * generatedValue를 설정하여 identity면 AI, auto 면 기본 등을 설정할 수 있다.
     * default는 auto.
     *
     * https://gmlwjd9405.github.io/2019/08/12/primary-key-mapping.html 참고
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer index;
    // 내 DB가 Integer라서 Integer로 했지만, DB에서는 id값이 BigInt로 설정되어 있기 때문에 Long이 일반적.
    //     다만 key값이 작다면 Integer여도 됨.
    //     https://reasontaek.tistory.com/13 참고.

    @ManyToOne
    @JoinColumn(name = "uid")
    private Member uid;

    @NotNull
    @Column(length = 50)
    private String title;

    @Column(columnDefinition = "LONGBLOB")
    private byte[] img;

    @Column(name = "b_content",columnDefinition = "TEXT")
    private String content;

    @NotNull
    @Column(length = 20)
    private String category;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;
}
