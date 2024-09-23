package com.idle.kb_i_dle_backend.consume.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsumeDTO {

    //outcome_user
    private int id;
    private int uid;
    private String outcomeExpenditureCategory;
    private double amount;
    private String descript;
    private String memo;

    //outcome_average
    private String householdHeadAgeGroup; // Age group of the household head
    private int outcome; // Outcome from the average table
    private double householdSize; // Size of the household
    private String quater; // Quarter information


    public ConsumeDTO () {

    }



}
