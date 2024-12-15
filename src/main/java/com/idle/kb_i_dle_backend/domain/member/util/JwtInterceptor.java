package com.idle.kb_i_dle_backend.domain.member.util;

import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    private JwtProcessor jwtProcessor;

    @Autowired
    public JwtInterceptor(JwtProcessor jwtProcessor) {
        this.jwtProcessor = jwtProcessor;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.replace("Bearer ", "");
            log.info("jwt JwtInterceptor token");

            try {
                Integer uid = jwtProcessor.getUid(token);
//                request.setAttribute("uid", uid);  // UID를 HttpServletRequest에 저장
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(uid, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid JWT Token");
            }
        } else {
            throw new IllegalArgumentException("Authorization header is missing or invalid");
        }

        return true; // 요청을 계속 처리하도록 반환
    }
}
