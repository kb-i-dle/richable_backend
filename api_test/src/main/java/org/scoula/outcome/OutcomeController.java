package org.scoula.outcome;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.*;

@Controller
@Slf4j
@RequestMapping("/outcome")
@PropertySource({"classpath:/application.properties"})
public class OutcomeController {
    @Value("${outcome.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public OutcomeController() {
        this.restTemplate = new RestTemplate();
    }

    @GetMapping
    public ResponseEntity<String> outcome() {
            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");

            // HttpEntity 객체 생성 (헤더만 필요)
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // API 호출하여 응답을 ResponseEntity로 반환
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    entity,
                    String.class // 문자열로 응답 받음
            );

            // 응답 로그 출력
            log.info("Response from API: " + response.getBody());

            // ResponseEntity 자체를 반환
            return response;
    }
}
