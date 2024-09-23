package com.idle.kb_i_dle_backend.consume.controller;

import com.idle.kb_i_dle_backend.consume.entity.Outcome;
import com.idle.kb_i_dle_backend.consume.service.ConsumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consume")
@RequiredArgsConstructor
@Slf4j
public class ConsumeController {

    private final ConsumeService consumeService;

    // Get all consume records
    @GetMapping("/all")
    public List<Outcome> getAllConsumes() {
        log.info("Fetching all consume records");
        return consumeService.findAll();
    }

    // Get consume records by UID
    @GetMapping("/uid/{uid}")
    public List<Outcome> getConsumesByUid(@PathVariable int uid) {
        log.info("Fetching consume records for UID: {}", uid);
        return consumeService.findByUid(uid);
    }

    // Save or update a consume record
    @PostMapping("/save")
    public Outcome saveConsume(@RequestBody Outcome consume) {
        log.info("Saving consume record: {}", consume);
        return consumeService.saveConsume(consume);
    }

    // Get consume records by household head age group (from outcome_average)
    @GetMapping("/age-group/{ageGroup}")
    public List<Outcome> getConsumesByHouseholdHeadAgeGroup(@PathVariable String ageGroup) {
        log.info("Fetching consume records for household head age group: {}", ageGroup);
        return consumeService.findByHouseholdHeadAgeGroup(ageGroup);
    }

    // Get consume records by outcome expenditure category
    @GetMapping("/category/{category}")
    public List<Outcome> getConsumesByOutcomeExpenditureCategory(@PathVariable String category) {
        log.info("Fetching consume records for category: {}", category);
        return consumeService.findByOutcomeExpenditureCategory(category);
    }

    // Get consume records by household head age group and category
    @GetMapping("/age-group/{ageGroup}/category/{category}")
    public List<Outcome> getConsumesByAgeGroupAndCategory(@PathVariable String ageGroup, @PathVariable String category) {
        log.info("Fetching consume records for age group: {} and category: {}", ageGroup, category);
        return consumeService.findByHouseholdHeadAgeGroupAndOutcomeExpenditureCategory(ageGroup, category);
    }

    // Get consume records by household size greater than a given value (from outcome_average)
    @GetMapping("/household-size/{size}")
    public List<Outcome> getConsumesByHouseholdSizeGreaterThan(@PathVariable double size) {
        log.info("Fetching consume records where household size is greater than: {}", size);
        return consumeService.findByHouseholdSizeGreaterThan(size);
    }
}
