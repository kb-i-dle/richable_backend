package com.idle.kb_i_dle_backend.domain.income.service.impl;

import com.idle.kb_i_dle_backend.config.exception.CustomException;
import com.idle.kb_i_dle_backend.domain.finance.repository.AssetSummaryRepository;
import com.idle.kb_i_dle_backend.domain.income.dto.IncomeDTO;
import com.idle.kb_i_dle_backend.domain.income.entity.Income;
import com.idle.kb_i_dle_backend.domain.income.repository.IncomeRepository;
import com.idle.kb_i_dle_backend.domain.income.service.IncomeService;
import com.idle.kb_i_dle_backend.domain.member.entity.Member;
import com.idle.kb_i_dle_backend.domain.member.service.MemberService;
import com.idle.kb_i_dle_backend.global.codes.ErrorCode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {

    private final MemberService memberService;
    private final IncomeRepository incomeRepository;
    private final AssetSummaryRepository assetSummaryRepository;

    private void validateOwnership(Member incomeOwner, Member requestUser) {
        if (!incomeOwner.getUid().equals(requestUser.getUid())) {
            throw new CustomException(ErrorCode.INVALID_OWNER, "You do not have permission to access this income.");
        }
    }

    @Override
    public List<IncomeDTO> getIncomeList(Integer uid) {
        Member tempMember = memberService.findMemberByUid(uid);
        List<Income> incomes = incomeRepository.findByUid(tempMember);

        if (incomes.isEmpty()) {
            throw new CustomException(ErrorCode.NO_INCOME_DATA, "소득 데이터를 찾을 수 없습니다.");
        }

        List<IncomeDTO> incomeList = new ArrayList<>();
        for (Income i : incomes) {
            incomeList.add(IncomeDTO.convertToDTO(i));
        }
        return incomeList;
    }

    @Override
    public long getIncomeSumInMonth(Integer uid, int year, int month) {
        Member tempUser = memberService.findMemberByUid(uid);
        List<Income> incomes = incomeRepository.findByUidAndYearAndMonth(tempUser, year, month);

        if (incomes.isEmpty()) {
            throw new CustomException(ErrorCode.NO_INCOME_DATA, "지정된 월에 대한 소득 데이터가 없습니다.");
        }

        return incomes.stream().mapToLong(Income::getAmount).sum();
    }

    @Override
    public IncomeDTO getIncomeByIndex(Integer uid, Integer index) {
        Member tempMember = memberService.findMemberByUid(uid);
        Income isIncome = incomeRepository.findByIndex(index)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_INCOME_DATA, "지정된 인덱스로 소득 데이터를 찾을 수 없습니다: " + index));

        validateOwnership(isIncome.getUid(), tempMember);
        return IncomeDTO.convertToDTO(isIncome);
    }

    @Override
    public IncomeDTO addIncome(Integer uid, IncomeDTO incomeDTO) {
        try {
            Member tempMember = memberService.findMemberByUid(uid);
            Income incomeEntity = IncomeDTO.convertToEntity(tempMember, incomeDTO);
            Income savedIncome = incomeRepository.save(incomeEntity);
            assetSummaryRepository.insertOrUpdateAssetSummary(uid);

            return IncomeDTO.convertToDTO(savedIncome);
        } catch (ParseException e) {
            throw new CustomException(ErrorCode.INCOME_PARSE_ERROR, "소득 날짜를 분석하지 못했습니다.", e);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INCOME_CREATION_FAILED, "소득을 추가하지 못했습니다.", e);
        }
    }

    @Transactional
    @Override
    public IncomeDTO updateIncome(Integer uid, IncomeDTO incomeDTO) {
        Member member = memberService.findMemberByUid(uid);
        Income existingIncome = incomeRepository.findByIndex(incomeDTO.getIncomeId())
                .orElseThrow(() -> new CustomException(ErrorCode.NO_INCOME_DATA, "ID로 소득을 찾을수 없습니다.: " + incomeDTO.getIncomeId()));

        validateOwnership(existingIncome.getUid(), member);

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            existingIncome.setDate(dateFormat.parse(incomeDTO.getIncomeDate()));
        } catch (ParseException e) {
            throw new CustomException(ErrorCode.INCOME_PARSE_ERROR, "소득 날짜를 분석하지 못했습니다.", e);
        }

        existingIncome.setType(incomeDTO.getType());
        existingIncome.setAmount(incomeDTO.getPrice());
        existingIncome.setDescript(incomeDTO.getContents());
        existingIncome.setMemo(incomeDTO.getMemo());

        Income savedIncome = incomeRepository.save(existingIncome);
        if (savedIncome == null) {
            throw new CustomException(ErrorCode.INCOME_UPDATE_FAILED, "소득 수정을 실패했습니다.");
        }
        assetSummaryRepository.insertOrUpdateAssetSummary(uid);
        return IncomeDTO.convertToDTO(savedIncome);
    }

    @Transactional
    @Override
    public Integer deleteIncomeByUidAndIndex(Integer uid, Integer index) {
        Member tempMember = memberService.findMemberByUid(uid);
        Income isIncome = incomeRepository.findByIndex(index)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_INCOME_DATA, "인덱스에서 소득을 찾을수 없습니다.: " + index));

        validateOwnership(isIncome.getUid(), tempMember);

        try {
            incomeRepository.deleteByIndex(index);
            assetSummaryRepository.insertOrUpdateAssetSummary(uid);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INCOME_DELETION_FAILED, "소득을 삭제하지 못했습니다..", e);
        }

        return index;
    }
}
