package org.scoula.deposite;

import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Log4j2
@Controller
@RequestMapping("/deposite")
@PropertySource("classpath:application.properties")
public class DepositeController {
    @Value("${deposite.key}")
    private String apiKey;

    @Value("${deposite.url}")
    private String apiUrl;

    private final DepositeService depositeService;
    private final RestTemplate restTemplate;

    public DepositeController(DepositeService depositeService) {
        this.depositeService = depositeService;
        this.restTemplate = new RestTemplate();
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

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            // 응답 데이터를 처리하고 MySQL에 저장
            JSONObject jsonResponse = new JSONObject(response.getBody());
            JSONObject jsonResponse2 = jsonResponse.getJSONObject("result");
            JSONArray baseList = jsonResponse2.getJSONArray("baseList");
            JSONArray optionList = jsonResponse2.getJSONArray("optionList");

            // 데이터베이스에 저장
            for (int i = 0; i < baseList.length(); i++) {
                JSONObject product = baseList.getJSONObject(i);
                JSONObject productOptions = optionList.getJSONObject(i);

                DepositeVO depositeVO = new DepositeVO();
                depositeVO.setFinCoNo(product.getLong("fin_co_no"));
                depositeVO.setKorCoNm(product.getString("kor_co_nm"));
                depositeVO.setFinPrdtCd(product.getString("fin_prdt_cd"));
                depositeVO.setFinPrdtNm(product.getString("fin_prdt_nm"));
                depositeVO.setRsrvTypeNm(productOptions.getString("rsrv_type_nm"));
                depositeVO.setSaveTrm(productOptions.getLong("save_trm"));
                depositeVO.setIntrRate(productOptions.getDouble("intr_rate"));
                depositeVO.setIntrRate2(productOptions.getDouble("intr_rate2"));

                // 서비스 계층을 통해 데이터베이스에 저장
                depositeService.saveDepositeProduct(depositeVO);
            }

            return new ResponseEntity<>("Data saved successfully", HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error fetching data: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
