package com.idle.kb_i_dle_backend.domain.goal.repository;

import com.idle.kb_i_dle_backend.domain.goal.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Integer> {
    Goal findByIndex(int index);
}