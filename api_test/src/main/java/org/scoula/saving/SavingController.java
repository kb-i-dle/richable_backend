package org.scoula.saving;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/saving")
@PropertySource("classpath:application.properties")
public class SavingController {
    @Value("${savings.key}")
    private String apiKey;

    @Value("${savings.url}")
    private String apiUrl;

    private final SavingService savingService;
    private final RestTemplate restTemplate;

    public SavingController(SavingService savingService) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).build());
        this.savingService = savingService;
        this.restTemplate = new RestTemplate(factory);
        this.restTemplate.getMessageConverters().add(new StringHttpMessageConverter(StandardCharsets.UTF_8));

    }

    @GetMapping("/update")
    public ResponseEntity<String> getAssetData() {
        try {
            // API URL에 인증키 추가
            String url = apiUrl + "?auth=" + apiKey + "&topFinGrpNo=020000&pageNo=1";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            log.error(url);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            // 리다이렉트 확인
            if (response.getStatusCode() == HttpStatus.TEMPORARY_REDIRECT) {
                String newUrl = response.getHeaders().getLocation().toString();
                log.info("Redirecting to new URL: " + newUrl);

                // 리다이렉트된 URL로 다시 요청
                response = restTemplate.exchange(newUrl, HttpMethod.GET, entity, String.class);
            }
            // 응답 상태 코드 확인
            if (response.getStatusCode() != HttpStatus.OK) {
                return new ResponseEntity<>("Failed to fetch data from API: " + response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // 응답이 null인지 확인
            if (response.getBody() == null) {
                return new ResponseEntity<>("API response is null", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // 응답 데이터를 처리하고 JSON으로 변환
            JSONObject jsonResponse = new JSONObject(response.getBody());
            JSONObject result = jsonResponse.getJSONObject("result");
            JSONArray baseList = result.getJSONArray("baseList");
            JSONArray optionList = result.getJSONArray("optionList");

            // 각 product와 option을 병합하여 SavingVO 생성
            for (int i = 0; i < baseList.length(); i++) {
                JSONObject product = baseList.getJSONObject(i);
                JSONObject productOptions = optionList.getJSONObject(i); // option과 동일 인덱스를 병합

                SavingVO savingVO = new SavingVO();
                savingVO.setFinCoNo(product.getString("fin_co_no"));
                savingVO.setKorCoNm(product.getString("kor_co_nm"));
                savingVO.setFinPrdtCd(product.getString("fin_prdt_cd"));
                savingVO.setFinPrdtNm(product.getString("fin_prdt_nm"));

                // optionList에서 가져온 데이터 설정
                savingVO.setRsrvTypeNm(productOptions.getString("intr_rate_type_nm"));
                savingVO.setSaveTrm(productOptions.getLong("save_trm"));
                savingVO.setIntrRate(productOptions.getDouble("intr_rate"));
                savingVO.setIntrRate2(productOptions.getDouble("intr_rate2"));

                // 서비스 계층을 통해 데이터베이스에 저장
                savingService.saveSavingProduct(savingVO);
            }

            return new ResponseEntity<>("Data saved successfully", HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error fetching data: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
