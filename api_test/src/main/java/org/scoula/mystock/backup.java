//package org.scoula.mystock;
//
//public class backup {
//}

//package org.scoula.mystock;
//
//
//import lombok.extern.slf4j.Slf4j;
//import org.scoula.stock.StockInfo;
//import org.scoula.stock.StockService;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.http.*;
//import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.io.IOException;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Controller
//@Slf4j
//@RequestMapping("/mystock")
//@PropertySource({"classpath:/application.properties"})
//public class MyStockController {
//
//    @Value("${mystock.tokenurl}")
//    private String tokenUrl;
//    @Value("${mystock.app_key}")
//    private String appKey;
//    @Value("${mystock.app_secret}")
//    private String appSecret;
//    @Value("${mystock.token}")
//    private String token;
//    @Value("${mystock.priceUrl}")
//    private String priceUrl;
//
//    private final RestTemplate restTemplate;
//    private StockService stockService;
//
//    public MyStockController() {
//        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
//    }
//
//    @GetMapping("/getToken")
//    public ResponseEntity<String> getAccessToken() {
//        // API 호출 URL 설정 (모의 서버를 사용하려면 MOCK_API_URL, 실전 서버는 REAL_API_URL 사용)
//        String apiUrl = tokenUrl;
//        // HTTP 헤더 설정
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Content-Type", "application/json; charset=UTF-8");
//        // 요청 바디 설정 (JSON 형식)
//        Map<String, String> body = new HashMap<>();
//        body.put("grant_type", "client_credentials");
//        body.put("appkey", appKey);
//        body.put("appsecret", appSecret);
//        // HTTP 요청 만들기
//        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);
//        try {
//            // API 호출 (POST 요청)
//            ResponseEntity<String> response = restTemplate.exchange(
//                    apiUrl,
//                    HttpMethod.POST,
//                    requestEntity,
//                    String.class
//            );
//            // 응답 처리
//            if (response.getStatusCode() == HttpStatus.OK) {
//                log.info("Access Token: " + response.getBody());
//                return ResponseEntity.ok(response.getBody());
//            } else {
//                log.error("Error occurred: " + response.getStatusCode());
//                return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
//            }
//        } catch (Exception e) {
//            log.error("Exception while requesting token: ", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while requesting token");
//        }
//    }
//
//    @GetMapping("/getprice")
//    public ResponseEntity<String> getprice() {
//        String apiUrl = priceUrl;
//        String priceUrlwP = UriComponentsBuilder.fromHttpUrl(priceUrl)
//                .queryParam("FID_COND_MRKT_DIV_CODE", "J")  // 예: 주식 (J)
//                .queryParam("FID_INPUT_ISCD", "000660")  // 종목번호 (예: SK하이닉스 000660)
//                .toUriString();
//        // HTTP 헤더 설정
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("content-Type", "application/json; charset=UTF-8");
//        headers.set("authorization", "Bearer " + token);
//        headers.set("appkey", appKey);
//        headers.set("appsecret", appSecret);
//        headers.set("tr_id", "FHKST01010100");
//        // 요청 바디 설정 (JSON 형식)
//        Map<String, String> body = new HashMap<>();
//
//        // HTTP 요청 만들기 (GET 요청이므로 요청 바디는 필요 없음)
//        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
//
//        try {
//            ResponseEntity<String> response = restTemplate.exchange(
//                    priceUrlwP,  // 쿼리 파라미터가 포함된 URL
//                    HttpMethod.GET,
//                    requestEntity,
//                    String.class
//            );
//
//            // 응답 처리
//            if (response.getStatusCode() == HttpStatus.OK) {
//                log.info("Response Data: " + response.getBody());
//                return ResponseEntity.ok(response.getBody());
//            } else {
//                log.error("Error occurred: " + response.getStatusCode());
//                log.error("url: " + priceUrlwP);
//                return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
//            }
//        } catch (Exception e) {
//            log.error("Exception while requesting stock price: ", e);
//            log.error("url: " + priceUrlwP);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while requesting stock price");
//        }
//    }
//
//    @GetMapping("/getAllStockPrices")
//    public ResponseEntity<String> getAllStockPrices() {
//        // 첫 번째 테이블에서 종목 정보를 불러옴
//        List<StockInfo> stocksList = stockService.getAllStocks();
//
//        // Collect API responses for each stock
//        List<String> stockPrices = stocksList.stream().map(stock -> {
//
//                    String apiUrl = priceUrl;
//                    String priceUrlwP = UriComponentsBuilder.fromHttpUrl(priceUrl)
//                            .queryParam("FID_COND_MRKT_DIV_CODE", "J")  // 예: 주식 (J)
//                            .queryParam("FID_INPUT_ISCD", stock.getStandardCode())  // 종목번호 (예: SK하이닉스 000660)
//                            .toUriString();
//                    // HTTP 헤더 설정
//                    HttpHeaders headers = new HttpHeaders();
//                    headers.set("content-Type", "application/json; charset=UTF-8");
//                    headers.set("authorization", "Bearer " + token);
//                    headers.set("appkey", appKey);
//                    headers.set("appsecret", appSecret);
//                    headers.set("tr_id", "FHKST01010100");
//
//                    // HTTP 요청 만들기 (GET 요청이므로 요청 바디는 필요 없음)
//                    HttpEntity<String> requestEntity = new HttpEntity<>(headers);
//
//                    // API call
//                    ResponseEntity<String> response = restTemplate.exchange(
//                            priceUrlwP,
//                            HttpMethod.GET,
//                            requestEntity,
//                            String.class
//                    );
//
//                    String responseBody = response.getBody();
//
//                    // Check if the response contains the specific error message
//                    if (responseBody != null && responseBody.contains("SERVICE_KEY_IS_NOT_REGISTERED_ERROR")) {
//                        // Log the error and skip this stock
//                        log.warn("Service key error for stock {}: {}", stock.getKoreanStockName(), responseBody);
//                        return null; // Return null or skip this stock
//                    }
//
//                    log.info("Response from API for stock {}: {}", stock.getKoreanStockName(), responseBody);
//                    return responseBody;  // Return the API response for this stock
//                }).filter(Objects::nonNull) // Filter out any null responses (skipped stocks)
//                .collect(Collectors.toList());
//
//        // Combine the results into a single response
//        String combinedResponse = String.join("\n", stockPrices);
//
//        // Return the combined API responses
//        return ResponseEntity.ok(combinedResponse);
//    }
//}


//private ResponseEntity<String> fetchStockPrice(String stockCode) {
//    // stockCode가 6자리가 되지 않으면 왼쪽에 0을 채워 6자리로 만듦
//    String paddedStockCode = stockCode.length() < 6 ? "0".repeat(6 - stockCode.length()) + stockCode : stockCode;
//
//    String priceUrlWithParams = UriComponentsBuilder.fromHttpUrl(priceUrl)
//            .queryParam("FID_COND_MRKT_DIV_CODE", "J")  // 주식 (J)
//            .queryParam("FID_INPUT_ISCD", paddedStockCode)  // 종목번호
//            .toUriString();
//
//    HttpHeaders headers = new HttpHeaders();
//    headers.set("Content-Type", "application/json; charset=UTF-8");
//    headers.set("authorization", "Bearer " + token);
//    headers.set("appkey", appKey);
//    headers.set("appsecret", appSecret);
//    headers.set("tr_id", "FHKST01010100");
//
//    HttpEntity<String> requestEntity = new HttpEntity<>(headers);
//
//    try {
//        Thread.sleep(50);
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                priceUrlWithParams,
//                HttpMethod.GET,
//                requestEntity,
//                String.class
//        );
//
//        if (response.getStatusCode() == HttpStatus.OK) {
//            log.info("Response Data for stock {}: {}", stockCode, response.getBody());
//            return ResponseEntity.ok(response.getBody());
//        } else {
//            log.error("Error occurred: " + response.getStatusCode());
//            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
//        }
//    } catch (Exception e) {
//        log.error("Exception while requesting stock price for stock {}: {}", stockCode, e);
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while requesting stock price");
//    }
//}