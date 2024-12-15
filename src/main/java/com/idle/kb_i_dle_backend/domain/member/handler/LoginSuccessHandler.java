package com.idle.kb_i_dle_backend.domain.member.handler;

import com.idle.kb_i_dle_backend.domain.member.dto.AuthResultDTO;
import com.idle.kb_i_dle_backend.domain.member.dto.CustomUserDetails;
import com.idle.kb_i_dle_backend.domain.member.dto.MemberInfoDTO;
import com.idle.kb_i_dle_backend.domain.member.util.JsonResponse;
import com.idle.kb_i_dle_backend.domain.member.util.JwtProcessor;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProcessor jwtProcessor;

    private AuthResultDTO makeAuthResult(CustomUserDetails user) {
        String username = user.getUsername();
        Integer uid = user.getMember().getUid();
        String nickname = user.getMember().getNickname();
        String email = user.getMember().getEmail();
        String auth = user.getAuthorities().toString();
        // Generate token
        String token = jwtProcessor.generateToken(username, uid, nickname, email);
        // Combine token and user info into AuthResultDTO
        MemberInfoDTO userInfo = new MemberInfoDTO(uid, username, nickname, auth, email);
        return new AuthResultDTO(token, userInfo);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // Get the authenticated user
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        // Generate the authentication result and send it as JSON
        AuthResultDTO result = makeAuthResult(user);
        JsonResponse.send(response, result);
    }
}