package org.scoula.stock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/stock")
@PropertySource({"classpath:/application.properties"})
public class StockController {

    @Value("${stock.url}")
    private String apiUrl;

    @Value("${openAPI.key}")
    private String key;

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    // 가장 가까운 평일을 매개변수로
//    private String getClosestPastWeekday() {
//        LocalDate today = LocalDate.now();
//        // Check if today is a weekend
//        if (today.getDayOfWeek() == DayOfWeek.SATURDAY) {
//            return today.minusDays(1).toString();  // Move to Friday
//        } else if (today.getDayOfWeek() == DayOfWeek.SUNDAY) {
//            return today.minusDays(2).toString();  // Move to Friday
//        } else {
//            return today.toString();  // It's a weekday, return today
//        }
//    }

    @GetMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getStockDataAndUpdateDB() {
        try {
            // Retrieve stock information from the database
            List<StockVO> stocks = stockService.getAllStocks();

            for (StockVO stock : stocks) {
                // Prepare the API request URL
                String requestUrl = apiUrl + "?serviceKey=" + key +
                        "&numOfRows=1" +     // Number of results per page
                        "&pageNo=1" +         // Page number (you can make this dynamic if needed)
                        "&resultType=json" +   // Response format (JSON in this case)
                        "&basDt=20240911" +
                        "&isinCd=" + stock.getStandardCode();  // Korean stock name

                // Use HttpURLConnection to make API request
                URL url = new URL(requestUrl);
                log.info(url.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                int responseCode = conn.getResponseCode();
                log.info("Response code for stock {}: {}", stock.getKoreanStockName(), responseCode);

                BufferedReader rd;
                if (responseCode >= 200 && responseCode <= 300) {
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                } else {
                    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                }

                // Read API response
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
                conn.disconnect();

                // Parse JSON and extract fields
                String responseBody = sb.toString();
                log.info("Response Body for stock {}: {}", stock.getKoreanStockName(), responseBody);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(responseBody);
                JsonNode items = rootNode.path("response").path("body").path("items").path("item");

                // Loop through the items and extract data
                for (JsonNode item : items) {
                    // Extract values
                    String standardCode = item.path("isinCd").asText();  // ISIN code (standardCode)
                    String price = item.path("clpr").asText();  // Closing price (clpr)

                    // Update data in the database using standardCode as the key
                    stockService.updateStockData(price,standardCode);
                }
            }

            // Return success response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>("Stock prices updated successfully", headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error occurred while fetching stock data and updating DB", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
