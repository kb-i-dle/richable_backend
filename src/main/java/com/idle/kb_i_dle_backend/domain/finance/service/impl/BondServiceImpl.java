package com.idle.kb_i_dle_backend.domain.finance.service.impl;

import com.idle.kb_i_dle_backend.config.exception.CustomException;
import com.idle.kb_i_dle_backend.domain.finance.dto.BondDTO;
import com.idle.kb_i_dle_backend.domain.finance.entity.Bond;
import com.idle.kb_i_dle_backend.domain.finance.repository.AssetSummaryRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.BondRepository;
import com.idle.kb_i_dle_backend.domain.finance.service.BondService;
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
public class BondServiceImpl implements BondService {

    private final MemberService memberService;
    private final BondRepository bondRepository;
    private final AssetSummaryRepository assetSummaryRepository;;

    @Override
    public List<BondDTO> getBondList(Integer uid) {
        Member member = memberService.findMemberByUid(uid);
        List<Bond> bonds = bondRepository.findByUidAndDeleteDateIsNull(member);

        if (bonds.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_BOND, "user dont have bond");
        }

        List<BondDTO> bondList = new ArrayList<>();
        for (Bond b : bonds) {
            BondDTO bondDTO = BondDTO.convertToDTO(b);
            bondList.add(bondDTO);
        }

        return bondList;
    }

    @Override
    public BondDTO addBond(Integer uid, BondDTO bondDTO) throws ParseException {
        Member member = memberService.findMemberByUid(uid);
        Bond savedBond = bondRepository.save(BondDTO.convertToEntity(member, bondDTO));
        assetSummaryRepository.insertOrUpdateAssetSummary(uid);
        //assetSummaryRepository.deleteDuplicateAssetSummary();
        return BondDTO.convertToDTO(savedBond);
    }

    @Transactional
    @Override
    public BondDTO updateBond(Integer uid, BondDTO bondDTO) {
        Member member = memberService.findMemberByUid(uid);

        // Bond 조회
        Bond isBond = bondRepository.findByIndexAndDeleteDateIsNull(bondDTO.getIndex())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_BOND, "user dont have bond"));

        // Bond의 소유자가 해당 User인지 확인
        if (!isBond.getUid().equals(member)) {
            throw new CustomException(ErrorCode.INVALID_OWNER, "You do not have permission to modify this bond");
        }

        // 보유량만 수정되게
        isBond.setCnt(bondDTO.getCnt());

        Bond savedBond = bondRepository.save(isBond);
        assetSummaryRepository.insertOrUpdateAssetSummary(uid);
        //assetSummaryRepository.deleteDuplicateAssetSummary();
        return BondDTO.convertToDTO(savedBond);
    }

    @Transactional
    @Override
    public BondDTO deleteBond(Integer uid, Integer index) {
        Member member = memberService.findMemberByUid(uid);

        // Bond 조회
        Bond isBond = bondRepository.findByIndexAndDeleteDateIsNull(index)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_BOND, "user dont have bond"));

        // Bond의 소유자가 해당 User인지 확인
        if (!isBond.getUid().equals(member)) {
            throw new CustomException(ErrorCode.INVALID_OWNER, "You do not have permission to modify this bond");
        }

        isBond.setDeleteDate(new Date());

        Bond savedBond = bondRepository.save(isBond);
        assetSummaryRepository.insertOrUpdateAssetSummary(uid);
        //assetSummaryRepository.deleteDuplicateAssetSummary();
        return BondDTO.convertToDTO(savedBond);
    }
}
