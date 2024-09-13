package org.scoula.coin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/coin")
@PropertySource({"classpath:/application.properties"})
public class CoinController {

    @Value("${coin_market.url}")
    private String URL;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final CoinService coinService;

    public CoinController(RestTemplate restTemplate, ObjectMapper objectMapper, CoinService coinService) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.coinService = coinService;
    }

    @GetMapping("")
    public ResponseEntity<Map<String, String>> getCoin() throws IOException {
        // Construct the full URL
        String fullUrl = UriComponentsBuilder.fromHttpUrl(URL).toUriString();

        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        // Create HttpEntity object
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Make the API call and get the response as String
        ResponseEntity<String> response = restTemplate.exchange(
                fullUrl,
                HttpMethod.GET,
                entity,
                String.class
        );

        // Create a map to store the closing prices
        Map<String, String> closingPrices = new HashMap<>();

        // Parse the JSON response
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        // Extract the "data" node
        JsonNode dataNode = jsonNode.get("data");
        if (dataNode != null && dataNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = dataNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String coin = entry.getKey();
                JsonNode coinData = entry.getValue();

                // Get the closing price
                JsonNode closingPriceNode = coinData.get("closing_price");
                String closingPrice = closingPriceNode != null ? closingPriceNode.asText() : "N/A";

                // Store the coin name and closing price in the map
                closingPrices.put(coin, closingPrice);
            }
        }

        // Save the closing prices to the database
//        coinService.saveCoinPrices(closingPrices);
        coinService.updateCoinPrices(closingPrices);

        // Return the closing prices as a response
        return ResponseEntity.ok(closingPrices);
    }
}
