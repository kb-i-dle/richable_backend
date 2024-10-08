package com.idle.kb_i_dle_backend.domain.member.filter;

import com.idle.kb_i_dle_backend.domain.member.service.CustomMemberDetailsService;
import com.idle.kb_i_dle_backend.domain.member.util.JwtProcessor;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
//@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer "; // 끝에 공백 있음

    private final JwtProcessor jwtProcessor;
    private final CustomMemberDetailsService customUserDetailsService;


    public JwtAuthenticationFilter(JwtProcessor jwtProcessor, CustomMemberDetailsService customUserDetailsService) {
        this.jwtProcessor = jwtProcessor;
        this.customUserDetailsService = customUserDetailsService;
    }

    private Authentication getAuthentication(String token) {
        String username = jwtProcessor.getId(token);
        UserDetails principal = customUserDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extract JWT from Authorization header
        String token = resolveToken(request);

        // Validate token and set authentication if valid
        if (token != null && jwtProcessor.validateToken(token)) {
            String username = jwtProcessor.getId(token);
            Integer uid = jwtProcessor.getUid(token);

            HttpSession session = request.getSession();
            session.setAttribute("uid", uid);
            // Load user details
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // Set authentication to the context

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }

    // Extract token from Authorization header (Bearer token)
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // Remove "Bearer " prefix
        }
        return null;
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}