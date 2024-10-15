package com.idle.kb_i_dle_backend.config;

import com.idle.kb_i_dle_backend.domain.member.filter.JwtAuthenticationFilter;
import com.idle.kb_i_dle_backend.domain.member.handler.CustomAccessDeniedHandler;
import com.idle.kb_i_dle_backend.domain.member.handler.CustomAuthenticationEntryPoint;
import com.idle.kb_i_dle_backend.domain.member.service.CustomMemberDetailsService;
import com.idle.kb_i_dle_backend.domain.member.util.JwtProcessor;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
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
@PropertySource("classpath:application.properties")
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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); // 쿠키나 인증 정보 허용

        config.setAllowedOrigins(Arrays.asList("https://richable.site", "http://richable.site", "http://localhost:5173",
                "http://localhost:4173"));
        config.addAllowedOriginPattern("*");

        config.addAllowedHeader("*"); // 모든 헤더 허용
        config.addAllowedMethod("*");

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
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   CustomMemberDetailsService userDetailsService) throws Exception {
        http
                .csrf().disable()
                .cors()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/member/login", "/member/naverCallback", "/member/naverlogin").permitAll()
                .antMatchers("/invest/**").authenticated()  // /invest/** 경로에 대해 인증 요구
                .antMatchers("/finance/**").authenticated()
                .antMatchers("/goal/**").authenticated()
                .antMatchers("/income/**").authenticated()
                .antMatchers("/outcome/**").authenticated()
                .antMatchers("/asset/**").authenticated()
                .antMatchers("/master/**").hasRole("ADMIN")
                .anyRequest().authenticated()
//                .antMatchers("/member/login", "/member/register", "/member/naverlogin", "/member/naverCallback").permitAll()  // 로그인 및 회원가입 관련 경로는 모두 허용
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtProcessor, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
