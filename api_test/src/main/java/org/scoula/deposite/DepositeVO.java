package org.scoula.deposite;

import lombok.Data;

@Data
public class DepositeVO {
    private long finCoNo;
    private String korCoNm;
    private String finPrdtCd;
    private String finPrdtNm;
    private String rsrvTypeNm;
    private long saveTrm;
    private double intrRate;
    private double intrRate2;
}
