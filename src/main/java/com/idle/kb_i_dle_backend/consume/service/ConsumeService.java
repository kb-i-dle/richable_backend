package com.idle.kb_i_dle_backend.consume.service;

import com.idle.kb_i_dle_backend.consume.entity.Outcome;

import java.util.List;

public interface ConsumeService {

    List<Outcome> findAll();

    List<Outcome> findByUid(int uid);

    Outcome saveConsume(Outcome consume);

    // New methods related to outcome_average fields
    List<Outcome> findByHouseholdHeadAgeGroup(String ageGroup);

    List<Outcome> findByOutcomeExpenditureCategory(String category);

    List<Outcome> findByHouseholdHeadAgeGroupAndOutcomeExpenditureCategory(String ageGroup, String category);

    List<Outcome> findByHouseholdSizeGreaterThan(double size);

}
