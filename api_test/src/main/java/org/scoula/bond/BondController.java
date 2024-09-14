package org.scoula.bond;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@RestController
@Slf4j
@RequestMapping("/bond")
@PropertySource({"classpath:/application.properties"})
public class BondController {

    @Value("${openAPI.key}")
    private String key;

    private final BondService bondService;

    public BondController(BondService bondService) {
        this.bondService = bondService;
    }

    @GetMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getBondDataWithConnectionAndSaveToDB() {
        try {
            // API 호출 URL 설정
            String apiUrl = "https://apis.data.go.kr/1160100/service/GetBondIssuInfoService/getBondBasiInfo";
            String requestUrl = apiUrl + "?serviceKey=" + key + "&numOfRows=10&pageNo=1&resultType=json&basDt=20240912";

            // HttpURLConnection을 사용하여 API 요청
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            int responseCode = conn.getResponseCode();
            log.info("Response code: " + responseCode);

            BufferedReader rd;
            if (responseCode >= 200 && responseCode <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
            }

            // API 응답 읽기
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();

            // JSON 파싱 및 필요한 필드 추출
            String responseBody = sb.toString();
            log.info("Response Body: " + responseBody);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode items = rootNode.path("response").path("body").path("items").path("item");

            for (JsonNode item : items) {
                // 필요한 값 추출
                BondVO bondvo = new BondVO();
                bondvo.setCrno(item.path("crno").asText());
                bondvo.setScrsItmsKcd(item.path("scrsItmsKcd").asText());
                bondvo.setIsinCd(item.path("isinCd").asText());
                bondvo.setScrsItmsKcdNm(item.path("scrsItmsKcdNm").asText());
                bondvo.setBondIsurNm(item.path("bondIsurNm").asText());
                bondvo.setIsinCdNm(item.path("isinCdNm").asText());

                // 데이터 저장
                bondService.saveBond(bondvo);
            }

            // 성공 응답 반환
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>("Data saved successfully", headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error occurred while fetching data and saving to DB", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
