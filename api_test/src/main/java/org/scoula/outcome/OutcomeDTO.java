package org.scoula.outcome;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OutcomeDTO {
    @JsonProperty("TBL_NM")
    private String tblNm;
    @JsonProperty("PRD_DE")
    private String prdDe;
    @JsonProperty("TBL_ID")
    private String tblId;
    @JsonProperty("ITM_NM")
    private String itmNm;
    @JsonProperty("ITM_NM_ENG")
    private String itmNmEng;
    @JsonProperty("ITM_ID")
    private String itmId;
    @JsonProperty("UNIT_NM")
    private String unitNm;
    @JsonProperty("ORG_ID")
    private String orgId;
    @JsonProperty("UNIT_NM_ENG")
    private String unitNmEng;
    @JsonProperty("C1_OBJ_NM")
    private String c1ObjNm;
    @JsonProperty("C1_OBJ_NM_ENG")
    private String c1ObjNmEng;
    @JsonProperty("C2_OBJ_NM")
    private String c2ObjNm;
    @JsonProperty("C2_OBJ_NM_ENG")
    private String c2ObjNmEng;
    @JsonProperty("DT")
    private String dt;
    @JsonProperty("PRD_SE")
    private String prdSe;
    @JsonProperty("C2")
    private String c2;
    @JsonProperty("C1")
    private String c1;
    @JsonProperty("C1_NM")
    private String c1Nm;
    @JsonProperty("C2_NM")
    private String c2Nm;
    @JsonProperty("C1_NM_ENG")
    private String c1NmEng;
    @JsonProperty("C2_NM_ENG")
    private String c2NmEng;
    @JsonProperty("LST_CHN_DE")
    private String lstChnDe;
//    private String tbl_nm;
//    private int prd_de;
//    private String itm_nm;
//    private String itm_nm_eng;
//    private String itm_id;
//    private String unit_nm;
//    private int ord_id;
//    private String unit_nm_eng;
//    private String c1_obj_nm;
//    private String c1_obj_nm_eng;
//    private String c2_obj_nm;
//    private String c2_obj_nm_eng;
//    private Integer dt;
//    private String prd_se;
//    private int c2;
//    private String c1;
//    private String c1_nm;
//    private String c2_nm;
//    private String c1_nm_eng;
//    private String c2_nm_eng;
//    private String lst_chn_de;

}
