package com.idle.kb_i_dle_backend.domain.income.service;

import com.idle.kb_i_dle_backend.domain.income.dto.IncomeDTO;
import java.text.ParseException;
import java.util.List;

public interface IncomeService {
    List<IncomeDTO> getIncomeList(Integer uid);

    long getIncomeSumInMonth(Integer uid, int year, int month);

    IncomeDTO getIncomeByIndex(Integer uid, Integer index);

    IncomeDTO addIncome(Integer uid, IncomeDTO incomeDTO);

    IncomeDTO updateIncome(Integer uid, IncomeDTO incomeDTO);

    Integer deleteIncomeByUidAndIndex(Integer uid, Integer index);

}
