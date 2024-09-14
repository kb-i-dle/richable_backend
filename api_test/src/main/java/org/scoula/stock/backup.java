//package org.scoula.stock;
//
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Controller
//@Slf4j
//@RequestMapping("/stock")
//@PropertySource({"classpath:/application.properties"})
//public class backup {
//
//    @Value("${stock.url}")
//    private String apiUrl;
//
//    @Value("${openAPI.key}")
//    private String key;
//
//    private final RestTemplate restTemplate;
//    private final StockService stockService;
//
//    public backup(StockService stockService) {
//        this.restTemplate = new RestTemplate();
//        this.stockService = stockService;
//    }
//
//    @GetMapping
//    public ResponseEntity<String> getStockPrices() {
//        // Retrieve stock information from the database
//        List<StockInfo> stocks = stockService.getAllStocks();
//
//        // Collect API responses for each stock
//        List<String> stockPrices = stocks.stream().map(stock -> {
//            // Prepare the query parameters
//            String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
//                    .queryParam("serviceKey", key)  // Service key
//                    .queryParam("basDt", "20240911")  // Base date (replace with dynamic date if needed)
//                    .queryParam("itmsNm", stock.getKoreanStockName())  // Stock name (Korean stock name in this case)
//                    .toUriString();
//            log.info("getStockPrices: " + url);
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
//
//            log.info("Response from API for stock {}: {}", stock.getKoreanStockName(), response.getBody());
//
//            return response.getBody();  // Return the API response for this stock
//        }).collect(Collectors.toList());
//
//        // Combine the results into a single response
//        String combinedResponse = String.join("\n", stockPrices);
//
//        // Return the combined API responses
//        return ResponseEntity.ok(combinedResponse);
//
//    }
//}
