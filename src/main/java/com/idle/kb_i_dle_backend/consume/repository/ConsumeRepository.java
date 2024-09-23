package com.idle.kb_i_dle_backend.consume.repository;

import com.idle.kb_i_dle_backend.consume.entity.Outcome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsumeRepository extends JpaRepository<Outcome, Integer> {

    // Query to find all records by user ID
    List<Outcome> findByUid(int uid);

    // Query to find by outcome expenditure category
    List<Outcome> findByOutcomeExpenditureCategory(String outcomeExpenditureCategory);

    // Query to find by household head age group from the outcome_average table fields
    List<Outcome> findByHouseholdHeadAgeGroup(String householdHeadAgeGroup);

    // Query to find by both household head age group and outcome expenditure category
    List<Outcome> findByHouseholdHeadAgeGroupAndOutcomeExpenditureCategory(String householdHeadAgeGroup, String outcomeExpenditureCategory);

    // Query to find by household size (from outcome_average)
    List<Outcome> findByHouseholdSizeGreaterThan(double householdSize);
}
