package com.idle.kb_i_dle_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.idle.kb_i_dle_backend")
@PropertySource("classpath:application.properties")
public class WebConfig implements WebMvcConfigurer {
    public WebConfig(){
        System.out.println("WebConfig created");
    }

    //controller에서  return "jsp페이지명";
    // /WEB-INF/views/jsp페이지명.jsp
    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    //front -> backend : 서로 다른 프로세스이다.
    //front 프로세스와  backend 프로세스는 서로 다르다
    //front에서 자바스크립트로  -> backend 에 접근시도하면 CORS error
    //CORS 해제는 헤더에 추가정보(이거 와도 돼)
    @Override
    public void addCorsMappings(CorsRegistry registry) {

        //CORS정책오류 해결
        registry.addMapping("/**")
                .allowedMethods("*")
                .allowedOriginPatterns("*")
                .allowedOrigins("http://localhost:5173");//frontdomain
        //.allowedMethods("GET", "POST", "get", "post"); //대소문자 다 써야 함

//        registry.addMapping("/**")
//                .allowedMethods("*")
//                .allowedOrigins("http://localhost:3000")
//                .allowedOriginPatterns("*")
//                .allowedMethods("GET", "POST", "get", "post") // 허용할 HTTP method
//                .allowCredentials(true) // 쿠키 인증 요청 허용
//                .maxAge(3000); // 원하는 시간만큼 pre-flight 리퀘스트를 캐싱

    }


    @Bean
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
