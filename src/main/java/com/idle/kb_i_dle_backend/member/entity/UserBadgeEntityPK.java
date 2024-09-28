package com.idle.kb_i_dle_backend.member.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;
//badgeentity-pk 추가함
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserBadgeEntityPK implements Serializable {

    private Integer uid;
    private Integer badge_no;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserBadgeEntityPK that = (UserBadgeEntityPK) o;
        return Objects.equals(uid, that.uid) && Objects.equals(badge_no, that.badge_no);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, badge_no);
    }
}
