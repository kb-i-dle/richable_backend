package com.idle.kb_i_dle_backend.domain.finance.service.impl;

import com.idle.kb_i_dle_backend.config.exception.CustomException;
import com.idle.kb_i_dle_backend.domain.finance.dto.PriceSumDTO;
import com.idle.kb_i_dle_backend.domain.finance.dto.SpotDTO;
import com.idle.kb_i_dle_backend.domain.finance.entity.Spot;
import com.idle.kb_i_dle_backend.domain.finance.repository.SpotRepository;
import com.idle.kb_i_dle_backend.domain.finance.service.SpotService;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.global.codes.ErrorCode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpotServiceImpl implements SpotService {

    private final SpotRepository spotRepository;

    private final MemberService memberService;

    // 카테고리별 현물 자산 총합
    @Override
    public PriceSumDTO getTotalPriceByCategory(Integer uid, String category) {

        Member member = memberService.findMemberByUid(uid);

        String result = category.equals("car") ? "자동차" :
                category.equals("elec") ? "전자기기" :
                        category.equals("brand") ? "브랜드" :
                                category.equals("luxury") ? "명품" :
                                        "기타";

        List<Spot> spots = spotRepository.findByUidAndCategoryAndDeleteDateIsNull(member, result);

        if (spots.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_SPOT, "user dont have spot");
        }

        PriceSumDTO priceSum = new PriceSumDTO(
                category,
                spots.stream()
                        .mapToLong(Spot::getPrice)
                        .sum());

        return priceSum;
    }

    // 전체 현물 자산 총합
    @Override
    public PriceSumDTO getTotalPrice(Integer uid) {
        Member member = memberService.findMemberByUid(uid);
        List<Spot> spots = spotRepository.findByUidAndDeleteDateIsNull(member);

        if (spots.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_SPOT);
        }

        PriceSumDTO priceSum = new PriceSumDTO(
                "현물자산",
                spots.stream()
                        .mapToLong(Spot::getPrice)
                        .sum());

        return priceSum;
    }

    // 현물 자산 목록 전체 조회
    @Override
    public List<SpotDTO> getSpotList(Integer uid) {
        Member member = memberService.findMemberByUid(uid);
        List<Spot> spots = spotRepository.findByUidAndDeleteDateIsNull(member);

        if (spots.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_SPOT);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<SpotDTO> spotList = new ArrayList<>();
        for (Spot s : spots) {
            SpotDTO spotDTO = new SpotDTO();
            spotDTO.setIndex(s.getIndex());
            spotDTO.setCategory(s.getCategory());
            spotDTO.setName(s.getName());
            spotDTO.setPrice(s.getPrice());
            String formattedAddDate = dateFormat.format(s.getAddDate());
            spotDTO.setAddDate(formattedAddDate);
            spotList.add(spotDTO);
        }

        return spotList;
    }

    // 현물 자산 추가
    @Override
    public SpotDTO addSpot(Integer uid, SpotDTO spotDTO) throws ParseException {
        Member member = memberService.findMemberByUid(uid);
        Spot savedSpot = spotRepository.save(SpotDTO.convertToEntity(member, spotDTO));

        return SpotDTO.convertToDTO(savedSpot);
    }

    // 현물 자산 수정
    @Transactional
    @Override
    public SpotDTO updateSpot(Integer uid, SpotDTO spotDTO) {
        Member member = memberService.findMemberByUid(uid);

        // Spot 조회
        Spot isSpot = spotRepository.findByIndexAndDeleteDateIsNull(spotDTO.getIndex())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_SPOT));

        // Spot의 소유자가 해당 User인지 확인
        if (!isSpot.getUid().equals(member)) {
            throw new CustomException(ErrorCode.INVALID_OWNER);
        }

        isSpot.setName(spotDTO.getName());
        isSpot.setPrice(spotDTO.getPrice());

        Spot savedSpot = spotRepository.save(isSpot);
        return SpotDTO.convertToDTO(savedSpot);
    }

    // 특정 유저와 index에 해당하는 Spot 삭제
    @Transactional
    @Override
    public SpotDTO deleteSpot(Integer uid, Integer index) {
        Member member = memberService.findMemberByUid(uid);

        // Spot 조회
        Spot isSpot = spotRepository.findByIndexAndDeleteDateIsNull(index)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_SPOT));

        // Spot의 소유자가 해당 User인지 확인
        if (!isSpot.getUid().equals(member)) {
            throw new CustomException(ErrorCode.INVALID_OWNER);
        }

        isSpot.setDeleteDate(new Date());

        Spot savedSpot = spotRepository.save(isSpot);
        return SpotDTO.convertToDTO(savedSpot);
    }

}