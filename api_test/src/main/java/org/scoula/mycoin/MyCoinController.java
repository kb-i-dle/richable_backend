package org.scoula.mycoin;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@Slf4j
@RequestMapping("/mycoin")
@PropertySource({"classpath:/application.properties"})
@RequiredArgsConstructor
public class MyCoinController {

    @Value("${mycoin.url}")
    private String URL;
    @Value("${mycoin.apiKey}")
    private String appKey;
    @Value("${mycoin.secretKey}")
    private String secretKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final MyCoinMapper myCoinMapper;  // Make this final and use dependency injection

    @GetMapping("")
    public ResponseEntity<Map<String, String>> getCoin() {
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
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            // Create a map to store the data from the response
            Map<String, String> coinData = new HashMap<>();
            coinData.put("response_status", String.valueOf(response.getStatusCode()));
            coinData.put("response_body", jsonNode.toString());

            // Log the JWT token and response for debugging
            log.info("JWT Token: {}", authenticationToken);
            log.info("Response Data: {}", response.getBody());

            // Return the parsed response as a Map
            return ResponseEntity.ok(coinData);

        } catch (Exception e) {
            log.error("Error occurred while fetching coin data: ", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch coin data"));
        }
    }

    @GetMapping("/balance")
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

            // Iterate through the JSON response array and insert or update coin data
            for (JsonNode coin : responseArray) {
                String currency = coin.get("currency").asText();
                double balance = coin.get("balance").asDouble();
                double avgBuyPrice = coin.get("avg_buy_price").asDouble();
                String unitCurrency = coin.get("unit_currency").asText();

                // Create CoinVO object to store the data
                MyCoinVO myCoinVO = new MyCoinVO();
                myCoinVO.setUid(1);  // Assuming a fixed UID for the user
                myCoinVO.setBalance(balance);
                myCoinVO.setAvgBuyPrice(avgBuyPrice);
                myCoinVO.setUnitCurrency(unitCurrency);
                myCoinVO.setCurrency(currency);

                // Use MyBatis to insert or update the coin data
                myCoinMapper.insertOrUpdateCoin(myCoinVO);  // Using the injected mapper now
            }

            return ResponseEntity.ok("Coin data updated successfully.");

        } catch (Exception e) {
            log.error("Error occurred while fetching or updating coin data: ", e);
            return ResponseEntity.status(500).body("Error occurred while updating coin data");
        }
    }
}
