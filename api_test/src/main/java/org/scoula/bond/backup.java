//package org.scoula.bond;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.http.*;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//@RestController
//@Slf4j
//@RequestMapping("/bond")
//@PropertySource({"classpath:/application.properties"})
//public class backup {
//
//    @Value("${stock.key}")
//    private String key;
//
//
//    private final RestTemplate restTemplate;
//
//    public backup() {
//        this.restTemplate = new RestTemplate();
//    }
//
//    @GetMapping("")
//    public ResponseEntity<String> getBondData() {
//        try {
//
//            String apiUrl = "https://apis.data.go.kr/1160100/service/GetBondIssuInfoService/getBondBasiInfo";
//
////            String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
////                    .queryParam("serviceKey", key)  // 인코딩되지 않은 키를 직접 추가
////                    .queryParam("numOfRows", "10")
////                    .queryParam("pageNo", "1")
////                    .queryParam("resultType", "json")
////                    .queryParam("basDt", "20240912")
////                    .toUriString();
//            // 인코딩된 serviceKey를 직접 추가하고 나머지 파라미터는 UriComponentsBuilder로 처리
//            String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
//                    .queryParam("numOfRows", "10")
//                    .queryParam("pageNo", "1")
//                    .queryParam("resultType", "json")
//                    .queryParam("basDt", "20240912")
//                    .toUriString();
//
//// 직접 인코딩된 serviceKey를 URL에 추가
//            url += "&serviceKey=" + key;
//
//            // HTTP Headers
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Accept", "application/json");
//
//            // HttpEntity for the request
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//
//            // API call
//            ResponseEntity<String> response = restTemplate.exchange(
//                    url,
//                    HttpMethod.GET,
//                    entity,
//                    String.class
//            );
//            log.error(url);
////            String response = restTemplate.getForObject(uri, String.class);
//            // 성공적으로 받은 응답을 그대로 반환
//            return ResponseEntity.ok(response.getBody());
//
//        } catch (Exception e) {
//            log.error("Error occurred while fetching data from API", e);
//
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//    // HttpURLConnection을 사용하는 새로운 메서드
//    @GetMapping("/connection")
//    public ResponseEntity<String> getBondDataWithConnection() {
//        try {
//            // API 호출을 위한 URL 생성
//            String apiUrl = "https://apis.data.go.kr/1160100/service/GetBondIssuInfoService/getBondBasiInfo";
//            String requestUrl = apiUrl + "?serviceKey=" + key + "&numOfRows=10&pageNo=1&resultType=json&basDt=20240912";
//
//            // URL 객체 생성
//            URL url = new URL(requestUrl);
//
//            // HttpURLConnection 객체 생성 및 설정
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");  // GET 방식 요청
//            conn.setRequestProperty("Content-type", "application/json");  // 요청 헤더 설정
//
//            // 응답 코드 확인 및 로그 출력
//            int responseCode = conn.getResponseCode();
//            log.info("Response code: " + responseCode);
//
//            BufferedReader rd;
//            if (responseCode >= 200 && responseCode <= 300) {
//                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            } else {
//                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//            }
//
//            // 응답 데이터를 읽어서 StringBuilder에 저장
//            StringBuilder sb = new StringBuilder();
//            String line;
//            while ((line = rd.readLine()) != null) {
//                sb.append(line);
//            }
//            rd.close();
//            conn.disconnect();
//
//            // 응답 데이터 로그 및 반환
//            String responseBody = sb.toString();
//            log.info("Response Body: " + responseBody);
//
//            return ResponseEntity.ok(responseBody);
//
//        } catch (Exception e) {
//            log.error("Error occurred while fetching data using HttpURLConnection", e);
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//}
