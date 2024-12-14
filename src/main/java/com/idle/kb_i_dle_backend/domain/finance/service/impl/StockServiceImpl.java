package com.idle.kb_i_dle_backend.domain.finance.service.impl;

import com.idle.kb_i_dle_backend.config.exception.CustomException;
import com.idle.kb_i_dle_backend.domain.finance.dto.StockDTO;
import com.idle.kb_i_dle_backend.domain.finance.entity.Stock;
import com.idle.kb_i_dle_backend.domain.finance.repository.AssetSummaryRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.StockRepository;
import com.idle.kb_i_dle_backend.domain.finance.service.StockService;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.global.codes.ErrorCode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final MemberService memberService;
    private final StockRepository stockRepository;
    private final AssetSummaryRepository assetSummaryRepository;

    @Override
    public List<StockDTO> getStockList(Integer uid) {
        Member member = memberService.findMemberByUid(uid);
        List<Stock> stocks = stockRepository.findByUidAndDeleteDateIsNull(member);

        if (stocks.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_STOCK, "user dont have stocks");
        }

        List<StockDTO> stockList = new ArrayList<>();
        for (Stock b : stocks) {
            StockDTO stockDTO = StockDTO.convertToDTO(b);
            stockList.add(stockDTO);
        }

        return stockList;
    }

    @Override
    public StockDTO addStock(Integer uid, StockDTO stockDTO) {
        Member member = memberService.findMemberByUid(uid);
        Stock savedStock = stockRepository.save(StockDTO.convertToEntity(member, stockDTO));
        assetSummaryRepository.insertOrUpdateAssetSummary(uid);
        //assetSummaryRepository.deleteDuplicateAssetSummary();
        return StockDTO.convertToDTO(savedStock);
    }

    @Transactional
    @Override
    public StockDTO updateStock(Integer uid, StockDTO stockDTO) {
        Member member = memberService.findMemberByUid(uid);

        // Stock 조회
        Stock isStock = (Stock) stockRepository.findByIndexAndDeleteDateIsNull(stockDTO.getIndex())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_STOCK));

        // Stock의 소유자가 해당 User인지 확인
        if (!isStock.getUid().equals(member)) {
            throw new CustomException(ErrorCode.INVALID_OWNER);
        }

        // 수량만 수정되게
        isStock.setHldgQty(stockDTO.getHldgQty());

        Stock savedStock = stockRepository.save(isStock);
        assetSummaryRepository.insertOrUpdateAssetSummary(uid);
        //assetSummaryRepository.deleteDuplicateAssetSummary();
        return StockDTO.convertToDTO(savedStock);
    }

    @Transactional
    @Override
    public StockDTO deleteStock(Integer uid, Integer index) {
        Member member = memberService.findMemberByUid(uid);

        // Stock 조회
        Stock isStock = (Stock) stockRepository.findByIndexAndDeleteDateIsNull(index)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_STOCK));

        // Stock의 소유자가 해당 User인지 확인
        if (!isStock.getUid().equals(member)) {
            throw new CustomException(ErrorCode.INVALID_OWNER);
        }
        isStock.setDeleteDate(new Date());

        Stock savedStock = stockRepository.save(isStock);
        assetSummaryRepository.insertOrUpdateAssetSummary(uid);
        //assetSummaryRepository.deleteDuplicateAssetSummary();
        return StockDTO.convertToDTO(savedStock);
    }
}
