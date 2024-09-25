package com.idle.kb_i_dle_backend.community.repository;

import com.idle.kb_i_dle_backend.community.entity.Board;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPARepository는 DB와 소통하는 하기위한 메서드를 제공한다.
 * <> 안에는 Return값인 매핑된 Entity, 그리고 해당 테이블의 ID값을 넣어준면 된다.
 * 문법으로는 findAll, findBy컬럼명 등 이 있는데 자동완성으로 보면서하면 대충 감이 온다.
 *
 */
public interface CommunityRepository extends JpaRepository<Board, Integer> {
    List<Board> findAll();
}
