package org.scoula.mystock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.scoula.stock.StockVO;
import org.scoula.stock.StockService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/mystock")
@PropertySource({"classpath:/application.properties"})
public class MyStockController {

    @Value("${mystock.tokenurl}")
    private String tokenUrl;
    @Value("${mystock.app_key}")
    private String appKey;
    @Value("${mystock.app_secret}")
    private String appSecret;
    @Value("${mystock.token}")
    private String token;
    @Value("${mystock.priceUrl}")
    private String priceUrl;
    @Value("${mystock.balanceUrl}")
    private String balanceUrl;


    private final RestTemplate restTemplate;
    private final StockService stockService;
    private final ObjectMapper objectMapper;

    public MyStockController(StockService stockService) {
        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        this.stockService = stockService;
        this.objectMapper = new ObjectMapper();
    }

    @GetMapping("/getToken")
    public ResponseEntity<String> getAccessToken() {
        String apiUrl = tokenUrl;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=UTF-8");

        Map<String, String> body = Map.of(
                "grant_type", "client_credentials",
                "appkey", appKey,
                "appsecret", appSecret
        );

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Access Token: " + response.getBody());
                return ResponseEntity.ok(response.getBody());
            } else {
                log.error("Error occurred: " + response.getStatusCode());
                return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
            }
        } catch (Exception e) {
            log.error("Exception while requesting token: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while requesting token");
        }
    }

    @GetMapping("/updateAllStockPrices")
    public ResponseEntity<String> updateAllStockPrices() {
        List<StockVO> stocksList = stockService.getAllStocks();  // Fetch all stocks from the database

        // Iterate over each stock and fetch/update its price
        stocksList.forEach(stock -> {
            String stockCode = stock.getShortCode();
            fetchAndUpdateStockPrice(stockCode);
            try {
                // Introduce a 0.5-second delay between API calls
                Thread.sleep(50);
            } catch (InterruptedException e) {
                log.error("Interrupted exception during delay between stock price updates", e);
                Thread.currentThread().interrupt();  // Restore interrupted status
            }
        });

        return ResponseEntity.ok("Stock prices updated for all stocks.");
    }

    private void fetchAndUpdateStockPrice(String stockCode) {
        String paddedStockCode = padStockCode(stockCode);
        String priceUrlWithParams = createPriceUrlWithParams(paddedStockCode);
        HttpHeaders headers = createHeaders();

        try {
            ResponseEntity<String> response = restTemplate.exchange(priceUrlWithParams, HttpMethod.GET, new HttpEntity<>(headers), String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Response Data for stock {}: {}", stockCode, response.getBody());

                // Parse the JSON response and update the stock price
                updateStockPriceInDatabase(stockCode, response.getBody());
            } else {
                log.error("Error occurred for stock {}: {}", stockCode, response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Exception while fetching stock price for stock {}: {}", stockCode, e);
        }
    }

    private void updateStockPriceInDatabase(String stockCode, String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode output = root.path("output");
        double price = output.path("stck_oprc").asDouble();  // Extract the stock price

        // Update the price in the database for the given stock
        MyStockService.getStockByShortCode(stockCode, price);
    }

    private String padStockCode(String stockCode) {
        return stockCode.length() < 6 ? "0".repeat(6 - stockCode.length()) + stockCode : stockCode;
    }

    private String createPriceUrlWithParams(String stockCode) {
        return UriComponentsBuilder.fromHttpUrl(priceUrl)
                .queryParam("FID_COND_MRKT_DIV_CODE", "J")  // 주식 (J)
                .queryParam("FID_INPUT_ISCD", stockCode)  // 종목번호
                .toUriString();
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=UTF-8");
        headers.set("authorization", "Bearer " + token);
        headers.set("appkey", appKey);
        headers.set("appsecret", appSecret);
        headers.set("tr_id", "FHKST01010100");
        return headers;
    }

    private ResponseEntity<String> executePostRequest(String url, HttpHeaders headers, Map<String, String> body) {
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Response: {}", response.getBody());
                return ResponseEntity.ok(response.getBody());
            } else {
                log.error("Error occurred: {}", response.getStatusCode());
                return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
            }
        } catch (Exception e) {
            log.error("Exception during POST request: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while executing request");
        }
    }
    @GetMapping("/balance")
    public ResponseEntity<String> balance() {
        String apiUrl = balanceUrl;
        String UrlwP = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("CANO", "68997941")
                .queryParam("ACNT_PRDT_CD", "01")
                .queryParam("AFHR_FLPR_YN", "N")
                .queryParam("OFL_YN", "")
                .queryParam("INQR_DVSN", "02")
                .queryParam("UNPR_DVSN", "01")
                .queryParam("FUND_STTL_ICLD_YN", "N")
                .queryParam("FNCG_AMT_AUTO_RDPT_YN", "N")
                .queryParam("PRCS_DVSN", "01")
                .queryParam("CTX_AREA_FK100", "")
                .queryParam("CTX_AREA_NK100", "")
                .toUriString();
        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-Type", "application/json; charset=UTF-8");
        headers.set("authorization", "Bearer " + token);
        headers.set("appkey", appKey);
        headers.set("appsecret", appSecret);
        headers.set("tr_id", "FHKST01010100");

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    UrlwP,  // 쿼리 파라미터가 포함된 URL
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            // 응답 처리
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Response Data(success): " + response.getBody());
                log.error("url: " + UrlwP);
                return ResponseEntity.ok(response.getBody());
            } else {
                log.error("Error occurred: " + response.getStatusCode());
                log.error("url: " + UrlwP);
                return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
            }
        } catch (Exception e) {
            log.error("Exception while requesting stock price: ", e);
            log.error("url: " + UrlwP);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while requesting balance");
        }
    }

}