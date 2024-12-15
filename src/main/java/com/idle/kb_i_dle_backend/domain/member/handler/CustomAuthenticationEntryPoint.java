package com.idle.kb_i_dle_backend.domain.member.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idle.kb_i_dle_backend.global.codes.ErrorCode;
import com.idle.kb_i_dle_backend.global.dto.ErrorResponseDTO;
import com.idle.kb_i_dle_backend.global.response.ErrorResponse;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // Return 401 Unauthorized

        log.info("Unauthorized user ");
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_UNAUTHOR);
        final ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(errorResponse);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        String result = objectMapper.writeValueAsString(errorResponseDTO);
        response.getWriter().write(result);
    }
}