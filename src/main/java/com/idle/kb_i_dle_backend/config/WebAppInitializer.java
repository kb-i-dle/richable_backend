package com.idle.kb_i_dle_backend.config;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    //Filterging => 한글 강제 인코딩 작업담당 필터
    @Override
    protected Filter[] getServletFilters() {
        //스프링이 제공해는 인코딩 클래스다
        CharacterEncodingFilter characterEncodingFilter
                = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return new Filter[]{characterEncodingFilter};
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] {  SecurityConfig.class, DBConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] { WebConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }
}
