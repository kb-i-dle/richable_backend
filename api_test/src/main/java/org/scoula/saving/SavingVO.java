package org.scoula.saving;

import lombok.Data;

@Data
public class SavingVO {
    // 기본 정보 필드
    private String finCoNo;         // 금융회사 번호
    private String korCoNm;         // 금융회사 이름
    private String finPrdtCd;       // 금융상품 코드
    private String finPrdtNm;       // 금융상품 이름


    // 옵션 정보 필드
    private String rsrvTypeNm;      // 이율 종류 명칭 (단리, 복리 등)
    private long saveTrm;           // 가입 기간 (단위: 개월)
    private double intrRate;        // 기본 이율
    private double intrRate2;       // 최고 우대 이율
}
