package com.idle.kb_i_dle_backend.domain.member.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerificationResult {
    private final boolean verified;
    private final String message;
    private final String id;       // ID 찾기 시 사용
    private final boolean canResetPassword;  // 비밀번호 재설정 시 사용

    public static VerificationResult success(String id) {
        return VerificationResult.builder()
                .verified(true)
                .message("인증이 성공적으로 완료되었습니다.")
                .id(id)
                .canResetPassword(true)
                .build();
    }

    public static VerificationResult fail(String message) {
        return VerificationResult.builder()
                .verified(false)
                .message(message)
                .canResetPassword(false)
                .build();
    }

}
