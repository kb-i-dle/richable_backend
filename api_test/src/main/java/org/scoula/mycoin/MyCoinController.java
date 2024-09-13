package org.scoula.mycoin;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.scoula.coin.CoinService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@Slf4j
@RequestMapping("/mycoin")
@PropertySource({"classpath:/application.properties"})
public class MyCoinController {

    @Value("${mycoin.url}")
    private String URL;
    @Value("${mycoin.apiKey}")
    private String appKey;
    @Value("${mycoin.secretKey}")
    private String secretKey;

    @Value("${jdbc.url}")
    private String dbUrl;
    @Value("${jdbc.username}")
    private String dbUsername;
    @Value("${jdbc.password}")
    private String dbPassword;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final CoinService coinService;

    public MyCoinController(RestTemplate restTemplate, ObjectMapper objectMapper, CoinService coinService) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.coinService = coinService;
    }

    @GetMapping("")
    public ResponseEntity<Map<String, String>> getCoin() {
        try {
            // Generate access token using JWT
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            String jwtToken = JWT.create()
                    .withClaim("access_key", appKey)  // access_key 추가
                    .withClaim("nonce", UUID.randomUUID().toString())  // 고유 nonce 값
                    .withClaim("timestamp", System.currentTimeMillis())  // 현재 시간 타임스탬프
                    .sign(algorithm);

            // Construct Authorization token in Bearer format
            String authenticationToken = "Bearer " + jwtToken;

            // Set HTTP headers, including Authorization
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authenticationToken);  // Bearer <JWT Token> 형식
            headers.set("Accept", "application/json");
            headers.set("Content-Type", "application/json; charset=UTF-8");  // 적절한 Content-Type 설정

            // Create HttpEntity object with headers
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Make the API call and get the response as String
            ResponseEntity<String> response = restTemplate.exchange(
                    URL,  // 올바른 URL 추가
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // Parse the response using ObjectMapper
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            // Create a map to store the data from the response
            Map<String, String> coinData = new HashMap<>();
            coinData.put("response_status", String.valueOf(response.getStatusCode()));
            coinData.put("response_body", jsonNode.toString());

            // 로그 출력으로 JWT 토큰 및 응답 확인
            log.info("JWT Token: {}", authenticationToken);
            log.info("Response Data: {}", response.getBody());

            // Return the parsed response as a Map
            return ResponseEntity.ok(coinData);

        } catch (Exception e) {
            log.error("Error occurred while fetching coin data: ", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch coin data"));
        }
    }
    @GetMapping("/updateCoinData")
    public ResponseEntity<String> updateCoinData() {
        try {
            // Generate access token using JWT
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            String jwtToken = JWT.create()
                    .withClaim("access_key", appKey)
                    .withClaim("nonce", UUID.randomUUID().toString())
                    .withClaim("timestamp", System.currentTimeMillis())
                    .sign(algorithm);

            // Construct Authorization token in Bearer format
            String authenticationToken = "Bearer " + jwtToken;

            // Set HTTP headers, including Authorization
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authenticationToken);
            headers.set("Accept", "application/json");
            headers.set("Content-Type", "application/json; charset=UTF-8");

            // Create HttpEntity object with headers
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Make the API call and get the response as String
            ResponseEntity<String> response = restTemplate.exchange(
                    URL,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // Parse the response using ObjectMapper
            JsonNode responseArray = objectMapper.readTree(response.getBody());

            // MySQL에 연결
            try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
                // PreparedStatement for inserting or updating coin data
                String insertSQL = "INSERT INTO coin (uid, balance, avg_buy_price, unit_currency, currency) " +
                        "VALUES (?,?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE balance = VALUES(balance), avg_buy_price = VALUES(avg_buy_price), unit_currency = VALUES(unit_currency)";

                for (JsonNode coin : responseArray) {
                    String currency = coin.get("currency").asText();
                    double balance = coin.get("balance").asDouble();
                    double avgBuyPrice = coin.get("avg_buy_price").asDouble();
                    String unitCurrency = coin.get("unit_currency").asText();

                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                        preparedStatement.setDouble(1, 1);
                        preparedStatement.setDouble(2, balance);
                        preparedStatement.setDouble(3, avgBuyPrice);
                        preparedStatement.setString(4, unitCurrency);
                        preparedStatement.setString(5, currency);

                        preparedStatement.executeUpdate();
                    }
                }
            }

            return ResponseEntity.ok("Coin data updated successfully.");

        } catch (Exception e) {
            log.error("Error occurred while fetching or updating coin data: ", e);
            return ResponseEntity.status(500).body("Error occurred while updating coin data");
        }
    }

}
