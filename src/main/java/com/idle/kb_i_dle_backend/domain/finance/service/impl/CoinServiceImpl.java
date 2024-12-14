package com.idle.kb_i_dle_backend.domain.finance.service.impl;

import com.idle.kb_i_dle_backend.config.exception.CustomException;
import com.idle.kb_i_dle_backend.domain.finance.dto.CoinDTO;
import com.idle.kb_i_dle_backend.domain.finance.entity.Coin;
import com.idle.kb_i_dle_backend.domain.finance.repository.AssetSummaryRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.CoinRepository;
import com.idle.kb_i_dle_backend.domain.finance.service.CoinService;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.global.codes.ErrorCode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoinServiceImpl implements CoinService {

    private final MemberService memberService;
    private final CoinRepository coinRepository;
    private final AssetSummaryRepository assetSummaryRepository;

    @Override
    public List<CoinDTO> getCoinList(Integer uid) {
        Member member = memberService.findMemberByUid(uid);
        List<Coin> coins = coinRepository.findByUidAndDeleteDateIsNull(member);

        if (coins.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_COIN, "user dont have coin");
        }

        List<CoinDTO> coinList = new ArrayList<>();
        for (Coin c : coins) {
            CoinDTO coinDTO = CoinDTO.convertToDTO(c);
            coinList.add(coinDTO);
        }

        return coinList;
    }

    @Override
    public CoinDTO addCoin(Integer uid, CoinDTO coinDTO) {
        Member member = memberService.findMemberByUid(uid);
        Coin savedCoin = coinRepository.save(CoinDTO.convertToEntity(member, coinDTO));
        assetSummaryRepository.insertOrUpdateAssetSummary(uid);
        //assetSummaryRepository.deleteDuplicateAssetSummary();
        return CoinDTO.convertToDTO(savedCoin);
    }

    @Transactional
    @Override
    public CoinDTO updateCoin(Integer uid, CoinDTO coinDTO) {
        Member member = memberService.findMemberByUid(uid);

        // Coin 조회
        Coin isCoin = coinRepository.findByIndexAndDeleteDateIsNull(coinDTO.getIndex())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_COIN,
                        "Coin not found with id: " + coinDTO.getIndex()));

        // Coin의 소유자가 해당 User인지 확인
        if (!isCoin.getUid().equals(member)) {
            throw new CustomException(ErrorCode.INVALID_OWNER, "You do not have permission to modify this coin.");
        }

        // 보유량만 수정되게
        isCoin.setBalance(coinDTO.getBalance());

        Coin savedCoin = coinRepository.save(isCoin);
        assetSummaryRepository.insertOrUpdateAssetSummary(uid);
        //assetSummaryRepository.deleteDuplicateAssetSummary();
        return CoinDTO.convertToDTO(savedCoin);
    }

    @Transactional
    @Override
    public CoinDTO deleteCoin(Integer uid, Integer index) {
        Member member = memberService.findMemberByUid(uid);

        // Coin 조회
        Coin isCoin = coinRepository.findByIndexAndDeleteDateIsNull(index)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_COIN, "Coin not found with id: " + index));

        // Coin의 소유자가 해당 User인지 확인
        if (!isCoin.getUid().equals(member)) {
            throw new CustomException(ErrorCode.INVALID_OWNER, "You do not have permission to modify this coin.");
        }

        isCoin.setDeleteDate(new Date());

        Coin savedCoin = coinRepository.save(isCoin);
        assetSummaryRepository.insertOrUpdateAssetSummary(uid);
        //assetSummaryRepository.deleteDuplicateAssetSummary();
        return CoinDTO.convertToDTO(savedCoin);
    }
}
