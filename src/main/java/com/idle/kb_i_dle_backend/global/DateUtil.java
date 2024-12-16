package com.idle.kb_i_dle_backend.global;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class DateUtil {

    private DateUtil() {
        // 유틸리티 클래스는 인스턴스화 방지
    }

    public static Date parseDateToUtilDate(String date, DateTimeFormatter formatter) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);
            return java.sql.Timestamp.valueOf(localDateTime); // java.util.Date로 변환
        } catch (DateTimeParseException e) {
            return null; // 파싱 실패 시 null 반환
        }
    }
}
