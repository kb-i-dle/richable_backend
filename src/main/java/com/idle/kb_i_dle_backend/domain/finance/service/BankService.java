package com.idle.kb_i_dle_backend.domain.finance.service;

import com.idle.kb_i_dle_backend.domain.finance.dto.BankDTO;
import java.text.ParseException;
import java.util.List;

public interface BankService {
    List<BankDTO> getBankList(Integer uid);

    BankDTO addBank(Integer uid, BankDTO bankDTO) throws ParseException;

    BankDTO updateBank(Integer uid, BankDTO bankDTO);

    BankDTO deleteBank(Integer uid, Integer index);
}
