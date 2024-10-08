package com.idle.kb_i_dle_backend.config;

import com.idle.kb_i_dle_backend.domain.member.filter.JwtAuthenticationFilter;
import com.idle.kb_i_dle_backend.domain.member.handler.CustomAccessDeniedHandler;
import com.idle.kb_i_dle_backend.domain.member.handler.CustomAuthenticationEntryPoint;
import com.idle.kb_i_dle_backend.domain.member.service.CustomMemberDetailsService;
import com.idle.kb_i_dle_backend.domain.member.util.JwtProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = {
        "com.idle.kb_i_dle_backend.domain.member.service",
        "com.idle.kb_i_dle_backend.domain.member.controller",
        "com.idle.kb_i_dle_backend.domain.member.filter",
        "com.idle.kb_i_dle_backend.domain.member.handler",
        "com.idle.kb_i_dle_backend.domain.member.util",
        "com.idle.kb_i_dle_backend.config"
})
public class SecurityConfig {

    private final JwtProcessor jwtProcessor;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfig(JwtProcessor jwtProcessor,
                          CustomAccessDeniedHandler accessDeniedHandler,
                          CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtProcessor = jwtProcessor;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 쿠키나 인증 정보 허용
        config.addAllowedOrigin("http://localhost:5173"); // 허용할 출처
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("https://nid.naver.com");
        config.addAllowedHeader("*"); // 모든 헤더 허용
        config.addAllowedMethod("*"); // 모든 메서드 허용 (GET, POST, PUT, DELETE, etc.)

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    /**
     * security 설정: CSRF 비활성화, JWT 및 CORS 필터 적용
     *
     * @param http
     * @param jwtAuthenticationFilter
     * @param userDetailsService
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter, CustomMemberDetailsService userDetailsService) throws Exception {
        http
                .csrf().disable()
                .addFilterBefore(corsFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthenticationFilter(jwtProcessor, userDetailsService), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/**").permitAll()
//                .antMatchers("/invest/**").authenticated()  // /invest/** 경로에 대해 인증 요구
//                .antMatchers("/member/login", "/member/register", "/member/naverlogin", "/member/naverCallback").permitAll()  // 로그인 및 회원가입 관련 경로는 모두 허용
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint);

        return http.build();
    }
}
