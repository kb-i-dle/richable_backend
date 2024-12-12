package com.idle.kb_i_dle_backend.global.codes;

import lombok.Getter;

@Getter
public enum ErrorCode {

    /**
     * ******************************* Global Error CodeList *************************************** HTTP Status Code
     * 400 : Bad Request 401 : Unauthorized 403 : Forbidden 404 : Not Found 500 : Internal Server Error
     * *********************************************************************************************
     */
    // 잘못된 서버 요청
    BAD_REQUEST_ERROR(400, "G001", "Bad Request Exception"),

    // @RequestBody 데이터 미 존재
    REQUEST_BODY_MISSING_ERROR(400, "G002", "Required request body is missing"),

    // 유효하지 않은 타입
    INVALID_TYPE_VALUE(400, "G003", " Invalid Type Value"),

    // Request Parameter 로 데이터가 전달되지 않을 경우
    MISSING_REQUEST_PARAMETER_ERROR(400, "G004", "Missing Servlet RequestParameter Exception"),

    // 입력/출력 값이 유효하지 않음
    IO_ERROR(400, "G005", "I/O Exception"),

    // com.google.gson JSON 파싱 실패
    JSON_PARSE_ERROR(400, "G006", "JsonParseException"),

    //특정 오류에 대한 세부 정보를 정의함
    PARSE_ERROR(400, "G013", "Parse Error occurred"),

    // com.fasterxml.jackson.core Processing Error
    JACKSON_PROCESS_ERROR(400, "G007", "com.fasterxml.jackson.core Exception"),

    // 권한이 없음
    FORBIDDEN_ERROR(403, "G008", "Forbidden Exception"),

    // 서버로 요청한 리소스가 존재하지 않음
    NOT_FOUND_ERROR(404, "G009", "Not Found Exception"),

    // NULL Point Exception 발생
    NULL_POINT_ERROR(404, "G010", "Null Point Exception"),

    // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
    NOT_VALID_ERROR(404, "G011", "handle Validation Exception"),

    // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
    NOT_VALID_HEADER_ERROR(404, "G012", "Header에 데이터가 존재하지 않는 경우 "),

    // 서버가 처리 할 방법을 모르는 경우 발생
    INTERNAL_SERVER_ERROR(500, "G999", "Internal Server Error Exception"),

    /**
     * ******************************* Custom Error CodeList ***************************************
     */
    // Transaction Insert Error
    INSERT_ERROR(200, "9999", "Insert Transaction Error Exception"),

    // Transaction Update Error
    UPDATE_ERROR(200, "9999", "Update Transaction Error Exception"),

    // Transaction Delete Error
    DELETE_ERROR(200, "9999", "Delete Transaction Error Exception"),

    INVALID_OWNER(200, "M02", "You do not have permission to modify this asset"), // End

    //member find error
    INVALID_MEMEBER(200, "M01", "Invalid Member Id"),

    //GOAL
    //invalid index
    INVALID_INDEX(200, "G01", "Invalid Index"),

    INVALID_PRIORITY(200, "G02", "Invalid Priority"),

    INVALID_CATEGORY(200, "G03", "Invalid Category"),

    INVALID_GOAL(200, "G04", "Invalid Goal"),

    //INCOME
    //Bond

    INVALID_BOND(200, "BO01", "user dont have bond"),

    //Bank
    INVALID_BANK(200, "B01", "user dont have banks"),

    //Coin
    INVALID_COIN(200, "B02", "user dont have coin"),

    //spot
    INVALID_SPOT(200, "S01", "user dont have spot"),

    INVALID_STOCK(200, "ST01", "USER DONT HAVE STOCK"),

    //INCOME
    INCOME_PARSE_ERROR(400, "I06", "Income date parse error"),

    INVALID_INCOME(404, "I01", "User does not have income data"),

    NO_INCOME_DATA(404, "I02", "No income data found"),

    INCOME_CREATION_FAILED(500, "I03", "Failed to create income"),

    INCOME_UPDATE_FAILED(500, "I04", "Failed to update income"),

    INCOME_DELETION_FAILED(500, "I05", "Failed to delete income"),



    //Invest
    NO_ASSETS_FOUND(200, "IV01", "No assets found for the user"),
    NO_INVESTMENT_DATA(200, "IV02", "No investment data found"),
    NO_CATEGORY_FOUND(200, "IV03", "No category found for the user"),
    NO_AVAILABLE_CASH(200, "IV04", "No available cash"),
    NO_RECOMMENDED_PRODUCTS(200, "IV05", "No recommended products found"),
    NO_HIGH_RETURN_STOCK(200, "IV06", "No high return stock products found"),
    NO_HIGH_RETURN_COIN(200, "IV07", "No high return coin products found"),
    NO_HIGH_RETURN_PRODUCTS(200, "IV08", "No high return products found"),

    //OUTCOME
    INVALID_OUTCOME(200, "O01", "User dont have outcome"),

    //member
    INVALID_UNAUTHOR(401, "M01", "Unauthorized"),
    USER_ALREADY_EXISTS(400, "M02", "already exist"),
    REGISTRATION_FAILED(400, "M03", "register failed"),
    EMAIL_NOT_FOUND(400, "M04", "email not found"),
    INVALID_VERIFICATION_CODE(400, "M05", "Invalid Verification Code"),
    MEMBER_NOT_FOUND(400, "M06", "member not found by id"),
    TOKEN_IS_NOT_VALID(401, "M07", "token is not valid"),
    PERMISSION_FORBIDDEN(403, "M08", "permission forbidden"),
    NAVER_LOGIN_FAILED(400, "M09", "navigate failed"),


    //S3
    INVALID_FILE(400, "S01", "Invalid File"),
    NO_SUCH_ALGO(500, "S02", "INVALID ALGO");;


    /**
     * ******************************* Error Code Constructor ***************************************
     */
    // 에러 코드의 '코드 상태'을 반환한다.
    private final int status;

    // 에러 코드의 '코드간 구분 값'을 반환한다.
    private final String divisionCode;

    // 에러 코드의 '코드 메시지'을 반환한다.
    private final String message;

    // 생성자 구성
    ErrorCode(final int status, final String divisionCode, final String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}