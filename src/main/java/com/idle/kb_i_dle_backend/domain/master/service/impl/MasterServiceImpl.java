package com.idle.kb_i_dle_backend.domain.master.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idle.kb_i_dle_backend.config.exception.CustomException;
import com.idle.kb_i_dle_backend.domain.finance.entity.StockPrice;
import com.idle.kb_i_dle_backend.domain.finance.repository.CoinPriceRepository;
import com.idle.kb_i_dle_backend.domain.finance.repository.StockPriceRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.idle.kb_i_dle_backend.domain.master.service.MasterService;
import com.idle.kb_i_dle_backend.global.codes.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

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
    @Scheduled(cron = "0 0 19 * * *") // 주식 시장이 끝나고 update
    public String updateStockPrices() {
        try {
            List<StockPrice> stocks = stockPriceRepository.findAllLatestStockInfo();
            ExecutorService executorService = Executors.newFixedThreadPool(20);

            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")); //"20241014";//
            log.info("current date: {}", currentDate);

            try {
                List<Callable<Boolean>> tasks = stocks.stream().map(stock -> (Callable<Boolean>) () -> {
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
                            log.info("API Response for {}: {}", stock.getStandard_code(), response.toString());

                            JsonNode rootNode = objectMapper.readTree(response.toString());
                            JsonNode items = rootNode.path("response").path("body").path("items").path("item");

                            for (JsonNode item : items) {
                                String standardCode = item.path("isinCd").asText();
                                Date date = java.sql.Date.valueOf(LocalDate.parse(currentDate, DateTimeFormatter.ofPattern("yyyyMMdd")));
                                int price = item.path("clpr").asInt();
                                stockPriceRepository.insertStockPrice(price, standardCode, date);
                            }

                            // 1ms 휴식 추가
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                throw new CustomException(ErrorCode.UPDATE_STOCKERROR, e.getMessage());
                            }
                            return true;
                        }
                    } catch (JsonProcessingException e) {
                        throw new CustomException(ErrorCode.JSON_PARSE_ERROR, "JSON 파싱 중 오류가 발생했습니다.");
                    } catch (Exception e) {
                        throw new CustomException(ErrorCode.UPDATE_STOCKERROR, e.getMessage());
                    }
                }).collect(Collectors.toList());

                List<Future<Boolean>> futures = executorService.invokeAll(tasks);
                int successCount = 0;
                int failCount = 0;
                for (Future<Boolean> future : futures) {
                    try {
                        Boolean result = future.get();
                        if (result != null && result) {
                            successCount++;
                        } else {
                            failCount++;
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new CustomException(ErrorCode.UPDATE_STOCKERROR, e.getMessage());
                    } catch (ExecutionException e) {
                        throw new CustomException(ErrorCode.UPDATE_STOCKERROR, e.getMessage());
                    }
                }
                log.info("Stock price update completed. Success: {}, Fail: {}", successCount, failCount);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CustomException(ErrorCode.UPDATE_STOCKERROR, e.getMessage());
            } finally {
                executorService.shutdown();
                try {
                    if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                        executorService.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executorService.shutdownNow();
                    Thread.currentThread().interrupt();
                    throw new CustomException(ErrorCode.UPDATE_STOCKERROR, e.getMessage() + " 종료 대기 중 인터럽트 발생");
                }
            }
            return "stock price update complete";
        } catch (Exception e) {
            throw new CustomException(ErrorCode.UPDATE_STOCKERROR, e.getMessage() + " 주식 가격 업데이트 실패");
        }
    }

    @Override
    @Transactional
    public String updateStockPricesBefore() {
        try {
            List<StockPrice> stocks = stockPriceRepository.findAllLatestStockInfo();

            // 현재 날짜를 "yyyyMMdd" 형식의 문자열로 변환
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            for (StockPrice stock : stocks) {
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
                } catch (JsonProcessingException e) {
                    throw new CustomException(ErrorCode.JSON_PARSE_ERROR, "JSON 파싱 중 오류가 발생했습니다.");
                } catch (Exception e) {
                    throw new CustomException(ErrorCode.UPDATE_STOCKERROR, e.getMessage());
                }
            }
            return "stock price update complete";
        } catch (Exception e) {
            throw new CustomException(ErrorCode.UPDATE_STOCKERROR, e.getMessage() + " 주식 가격 업데이트 실패");
        }
    }

    @Value("${coin_market.url}")
    private String coinMarketUrl;

    @Override
    @Transactional
    @Scheduled(cron = "0 0 10 * * *")
    // 매일 오전 10시에 실행 //cron 표현식 0: 분 (0분), 0: 시간 (0시),10: 일 (10시), 1: 월의 날짜 (1일),*: 월 (모든 월),?: 요일 (특정 요일 지정하지 않음)
    public String updateCoinPrices() {

        try {
            String response = restTemplate.getForObject(coinMarketUrl, String.class);
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
            return "coin price update complete";
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.JSON_PARSE_ERROR, "코인 데이터 JSON 파싱 중 오류가 발생했습니다.");
        } catch (Exception e) {
            throw new CustomException(ErrorCode.NO_INVESTMENT_DATA, e.getMessage() + " 코인 가격 업데이트 중 오류가 발생했습니다.");
        }
    }
}
