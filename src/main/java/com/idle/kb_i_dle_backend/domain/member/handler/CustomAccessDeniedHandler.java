package com.idle.kb_i_dle_backend.domain.member.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idle.kb_i_dle_backend.global.codes.ErrorCode;
import com.idle.kb_i_dle_backend.global.dto.ErrorResponseDTO;
import com.idle.kb_i_dle_backend.global.response.ErrorResponse;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        // Return 403 Forbidden
        log.info("forbidden ");
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.FORBIDDEN_ERROR);
        final ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(errorResponse);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        String result = objectMapper.writeValueAsString(errorResponseDTO);
        response.getWriter().write(result);
    }
}