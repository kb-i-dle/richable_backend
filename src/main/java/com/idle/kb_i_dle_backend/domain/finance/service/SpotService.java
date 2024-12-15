package com.idle.kb_i_dle_backend.domain.finance.service;

import com.idle.kb_i_dle_backend.domain.finance.dto.PriceSumDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.SpotDTO;
import java.text.ParseException;
import java.util.List;

public interface SpotService {
    PriceSumDTO getTotalPriceByCategory(Integer uid, String category);

    PriceSumDTO getTotalPrice(Integer uid);

    List<SpotDTO> getSpotList(Integer uid);

    SpotDTO addSpot(Integer uid, SpotDTO spotDTO) throws ParseException;

    SpotDTO updateSpot(Integer uid, SpotDTO spotDTO);

    SpotDTO deleteSpot(Integer uid, Integer index);

}
