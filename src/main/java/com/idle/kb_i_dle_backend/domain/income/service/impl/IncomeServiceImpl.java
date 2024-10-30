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

    @Override
    public List<IncomeDTO> getIncomeList(Integer uid) throws Exception {
        Member tempMember = memberService.findMemberByUid(uid);
        List<Income> incomes = incomeRepository.findByUid(tempMember);

        if (incomes.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INCOME);
        }

        List<IncomeDTO> incomeList = new ArrayList<>();
        for (Income i : incomes) {
            IncomeDTO incomeDTO = IncomeDTO.convertToDTO(i);
            incomeList.add(incomeDTO);
        }

        return incomeList;
    }

    @Override
    public long getIncomeSumInMonth(Integer uid, int year, int month) {
        Member tempUser = memberService.findMemberByUid(uid);
        List<Income> incomes = incomeRepository.findByUidAndYearAndMonth(tempUser, year, month);
        Long sumOfIncomes = incomes.stream().mapToLong(Income::getAmount).sum();

        return sumOfIncomes;
    }


    @Override
    public IncomeDTO getIncomeByIndex(Integer uid, Integer index) {
        Member tempMember = memberService.findMemberByUid(uid);
        Income isIncome = incomeRepository.findByIndex(index)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.INVALID_INCOME, "Income not found with index: " + index));

        // 유저가 소유한 income인지 확인
        if (!isIncome.getUid().getUid().equals(tempMember.getUid())) {
            throw new CustomException(ErrorCode.INVALID_OWNER, "You do not have permission to delete this income.");
        }

        return IncomeDTO.convertToDTO(isIncome);
    }

    @Override
    public IncomeDTO addIncome(Integer uid, IncomeDTO incomeDTO) throws ParseException {
        Member tempMember = memberService.findMemberByUid(uid);
        Income savedIncome = incomeRepository.save(IncomeDTO.convertToEntity(tempMember, incomeDTO));
        assetSummaryRepository.insertOrUpdateAssetSummary(uid);
        //assetSummaryRepository.deleteDuplicateAssetSummary();
        return IncomeDTO.convertToDTO(savedIncome);
    }

    @Transactional
    @Override
    public IncomeDTO updateIncome(Integer uid, IncomeDTO incomeDTO) throws ParseException {
        Member member = memberService.findMemberByUid(uid);

        // Income 조회
        Income isIncome = incomeRepository.findByIndex(incomeDTO.getIncomeId())
                .orElseThrow(
                        () -> new CustomException(ErrorCode.INVALID_INCOME,
                                "Income not found with id: " + incomeDTO.getIncomeId()));

        // income의 소유자가 해당 User인지 확인
        if (!isIncome.getUid().equals(member)) {
            throw new CustomException(ErrorCode.INVALID_OWNER, "You do not have permission to modify this income.");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        isIncome.setType(incomeDTO.getType());
        isIncome.setDate(dateFormat.parse(incomeDTO.getIncomeDate()));
        isIncome.setAmount(incomeDTO.getPrice());
        isIncome.setDescript(incomeDTO.getContents());
        isIncome.setMemo(incomeDTO.getMemo());

        Income savedIncome = incomeRepository.save(isIncome);
        assetSummaryRepository.insertOrUpdateAssetSummary(uid);
        //assetSummaryRepository.deleteDuplicateAssetSummary();
        return IncomeDTO.convertToDTO(savedIncome);
    }

    // 특정 유저와 index에 해당하는 소득 삭제
    @Transactional
    @Override
    public Integer deleteIncomeByUidAndIndex(Integer uid, Integer index) {
        Member tempMember = memberService.findMemberByUid(uid);
        Income isIncome = incomeRepository.findByIndex(index)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.INVALID_INCOME, "Income not found with index: " + index));

        // 유저가 소유한 소득인지 확인
        if (!isIncome.getUid().getUid().equals(tempMember.getUid())) {
            throw new CustomException(ErrorCode.INVALID_OWNER, "You do not have permission to delete this income.");
        }

        incomeRepository.deleteByIndex(index);  // income 삭제
        assetSummaryRepository.insertOrUpdateAssetSummary(uid);
        //assetSummaryRepository.deleteDuplicateAssetSummary();

        return index;
    }
}
