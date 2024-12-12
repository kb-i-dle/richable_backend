package com.idle.kb_i_dle_backend.domain.finance.service;

import com.idle.kb_i_dle_backend.domain.finance.dto.BondDTO;
import java.text.ParseException;
import java.util.List;

public interface BondService {
    List<BondDTO> getBondList(Integer uid);

    BondDTO addBond(Integer uid, BondDTO bondDTO);

    BondDTO updateBond(Integer uid, BondDTO bondDTO);

    BondDTO deleteBond(Integer uid, Integer index);
}
