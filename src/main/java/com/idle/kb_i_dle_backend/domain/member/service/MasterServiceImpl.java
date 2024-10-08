package com.idle.kb_i_dle_backend.domain.member.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idle.kb_i_dle_backend.domain.finance.entity.StockPrice;
import com.idle.kb_i_dle_backend.domain.finance.repository.CoinPriceRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.StockPriceRepository;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class MasterServiceImpl implements MasterService {

    private final StockPriceRepository stockPriceRepository;
    private final CoinPriceRepository coinPriceRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${stock.url}")
    private String apiUrl;

    @Value("${openAPI.key}")
    private String key;

    @Override
    @Transactional
    public void updateStockPrices() {
        List<StockPrice> stocks = stockPriceRepository.findAllLatestStockInfo();
        ExecutorService executorService = Executors.newFixedThreadPool(20);

        // 현재 날짜를 "yyyyMMdd" 형식의 문자열로 변환
        String currentDate = "20241002";
//                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));


        try {
            List<Callable<Void>> tasks = stocks.stream().map(stock -> (Callable<Void>) () -> {
                try {
                    String requestUrl = apiUrl + "?serviceKey=" + key +
                            "&numOfRows=1&pageNo=1&resultType=json&basDt=" + currentDate + "&isinCd=" + stock.getStandard_code();

                    URL url = new URL(requestUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                    try (BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();

                        String line;
                        while ((line = rd.readLine()) != null) {
                            response.append(line);
                        }
                        log.error("API Response: {}", response.toString());

                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode rootNode = objectMapper.readTree(response.toString());
                        JsonNode items = rootNode.path("response").path("body").path("items").path("item");

                        for (JsonNode item : items) {
                            String standardCode = item.path("isinCd").asText();
                            // 현재 날짜를 java.util.Date 객체로 변환
                            Date date = java.sql.Date.valueOf(LocalDate.parse(currentDate, DateTimeFormatter.ofPattern("yyyyMMdd")));
                            int price = item.path("clpr").asInt();
                            stockPriceRepository.insertStockPrice(price, standardCode, date);
                        }
                    }
                } catch (Exception e) {
                    log.error("Error updating stock prices", e);
                }
                return null;
            }).collect(Collectors.toList());

            List<Future<Void>> futures = executorService.invokeAll(tasks);
            for (Future<Void> future : futures) {
                future.get();
            }
        } catch (Exception e) {
            log.error("Error updating stock prices", e);
        } finally {
            executorService.shutdown();
        }
    }

    @Value("${coin_market.url}")
    private String coinMarketUrl;

    @Override
    @Transactional
    public void updateCoinPrices() {
        String response = restTemplate.getForObject(coinMarketUrl, String.class);

        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode dataNode = jsonNode.get("data");

            if (dataNode != null && dataNode.isObject()) {
                Date currentDate = new Date();

                dataNode.fields().forEachRemaining(entry -> {
                    String coinName = entry.getKey();
                    JsonNode coinData = entry.getValue();
                    JsonNode closingPriceNode = coinData.get("closing_price");

                    if (closingPriceNode != null) {
                        double closingPrice = closingPriceNode.asDouble();
                        coinPriceRepository.updateCoinPrice(coinName, closingPrice, currentDate);
                    }
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating coin prices", e);
        }
    }

    }
