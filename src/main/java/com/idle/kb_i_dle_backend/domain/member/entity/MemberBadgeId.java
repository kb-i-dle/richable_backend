package com.idle.kb_i_dle_backend.domain.member.entity;

import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;

@Getter
public class MemberBadgeId implements Serializable {
    private Integer uid;
    private Integer badgeNo;

    // equals, hashCode 구현
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemberBadgeId that = (MemberBadgeId) o;
        return Objects.equals(uid, that.uid) && Objects.equals(badgeNo, that.badgeNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, badgeNo);
    }

}
