package com.idle.kb_i_dle_backend.domain.finance.service;

import com.idle.kb_i_dle_backend.domain.finance.dto.CoinDTO;
import java.text.ParseException;
import java.util.List;

public interface CoinService {
    List<CoinDTO> getCoinList(Integer uid);

    CoinDTO addCoin(Integer uid, CoinDTO coinDTO);

    CoinDTO updateCoin(Integer uid, CoinDTO coinDTO);

    CoinDTO deleteCoin(Integer uid, Integer index);
}
