package com.idle.kb_i_dle_backend.domain.invest.service;

import com.idle.kb_i_dle_backend.domain.invest.dto.*;

import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import java.util.List;
import java.util.Optional;

public interface InvestService {
    List<InvestDTO> getInvestList(int uid) throws  Exception;

    AvailableCashDTO getAvailableCash(int uid);

    List<CategorySumDTO> getInvestmentTendency(int uid) throws Exception;

    long totalAsset(int uid) throws Exception;

    MaxPercentageCategoryDTO getMaxPercentageCategory(int uid);

    List<RecommendedProductDTO> getRecommendedProducts(int uid);

    List<HighReturnProductDTO> getHighReturnStock(int uid);

    List<HighReturnProductDTO> getHighReturnCoin(int uid);

    HighReturnProductsDTO getHighReturnProducts(int uid);
}
