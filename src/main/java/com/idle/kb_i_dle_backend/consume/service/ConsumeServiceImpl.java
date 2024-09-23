package com.idle.kb_i_dle_backend.consume.service;

import com.idle.kb_i_dle_backend.consume.entity.Outcome;
import com.idle.kb_i_dle_backend.consume.repository.ConsumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // 스프링 서비스 컴포넌트로 등록
public class ConsumeServiceImpl implements ConsumeService {

    @Autowired
    private ConsumeRepository consumeRepository;

    @Override
    public List<Outcome> findAll() {
        return consumeRepository.findAll();
    }

    @Override
    public List<Outcome> findByUid(int uid) {
        return consumeRepository.findByUid(uid);
    }

    @Override
    public Outcome saveConsume(Outcome consume) {
        return consumeRepository.save(consume);
    }

    // New methods related to outcome_average fields

    @Override
    public List<Outcome> findByHouseholdHeadAgeGroup(String ageGroup) {
        return consumeRepository.findByHouseholdHeadAgeGroup(ageGroup);
    }

    @Override
    public List<Outcome> findByOutcomeExpenditureCategory(String category) {
        return consumeRepository.findByOutcomeExpenditureCategory(category);
    }

    @Override
    public List<Outcome> findByHouseholdHeadAgeGroupAndOutcomeExpenditureCategory(String ageGroup, String category) {
        return consumeRepository.findByHouseholdHeadAgeGroupAndOutcomeExpenditureCategory(ageGroup, category);
    }

    @Override
    public List<Outcome> findByHouseholdSizeGreaterThan(double size) {
        return consumeRepository.findByHouseholdSizeGreaterThan(size);
    }
}
