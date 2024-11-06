package com.idle.kb_i_dle_backend.domain.income;

import com.idle.kb_i_dle_backend.config.exception.CustomException;
import com.idle.kb_i_dle_backend.global.codes.ErrorCode;

public class IncomeException extends CustomException {
    public IncomeException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static IncomeException InvalidIndexException() {
        return new IncomeException(ErrorCode.INVALID_INDEX, "Income not found with index");
    }
}
