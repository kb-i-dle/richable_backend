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
    public String outcome(Model model) {
        try {
            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");

            // HttpEntity 객체 생성 (헤더만 필요)
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    entity,
                    String.class // 응답을 일단 문자열로 받음
            );

            // 응답 내용을 로그로 출력
            log.info("Response from API: " + response.getBody());

            // 받은 문자열을 Object[]로 변환 시도 (JSON 형식이라고 가정)
            ObjectMapper objectMapper = new ObjectMapper();
            Object[] jsonResponse = objectMapper.readValue(response.getBody(), Object[].class);

            // 받아온 데이터를 모델에 추가
            model.addAttribute("outcomes", Arrays.asList(jsonResponse));

        } catch (Exception e) {
            log.error("Error occurred while fetching data from API", e);
            model.addAttribute("outcomes", Collections.emptyList()); // 실패 시 빈 리스트 전달
        }

        return "outcome/outcome";  // outcome 뷰 반환
    }
}
