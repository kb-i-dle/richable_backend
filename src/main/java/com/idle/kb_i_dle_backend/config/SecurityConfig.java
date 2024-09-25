package com.idle.kb_i_dle_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig{
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();


        config.setAllowCredentials(true); // 쿠키나 인증 정보 허용
        config.addAllowedOrigin("http://localhost:5173"); // 허용할 출처
        config.addAllowedHeader("*"); // 모든 헤더 허용
        config.addAllowedMethod("*"); // 모든 메서드 허용 (GET, POST, PUT, DELETE, etc.)

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    /**
     * security csrf설정, corsfilter를 거치고
     * 요청url에 대한 권한 확인을 한다.
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .addFilterBefore(corsFilter(), UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests((authorize) -> authorize
                        .antMatchers("/").permitAll()
                        .antMatchers("/login").permitAll()
                        .antMatchers("/register").permitAll()
                        .anyRequest().permitAll()
//                        .anyRequest().authenticated()  로그인 개발 전까지는 permitall
                )
                .httpBasic(withDefaults())
                .formLogin(withDefaults()); //로그인 페이지 만들면 login페이지 설정 예정

        return http.build();
    }

}
