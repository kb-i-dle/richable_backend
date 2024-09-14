package org.scoula.deposite;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

//@Controller
//@RequestMapping("/deposite")
//@PropertySource("classpath:application.properties")
public class backup {
    @Value("${deposite.key}")
    private String apiKey;

    @Value("${deposite.url}")
    private String apiUrl;

    @Value("${jdbc.url}")
    private String dbUrl;

    @Value("${jdbc.username}")
    private String dbUsername;

    @Value("${jdbc.password}")
    private String dbPassword;

    private final RestTemplate restTemplate;

    public backup() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.getMessageConverters().add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    @GetMapping("/update")
    public ResponseEntity<String> getAssetData() {
        try {
            // API URL에 인증키 추가
            String url = apiUrl + "?auth=" + apiKey + "&topFinGrpNo=020000&pageNo=1";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            // 응답 데이터를 처리하고 MySQL에 저장
            JSONObject jsonResponse = new JSONObject(response.getBody());
            JSONObject jsonResponse2 = jsonResponse.getJSONObject("result");
            JSONArray baseList = jsonResponse2.getJSONArray("baseList");
            JSONArray optionList = jsonResponse2.getJSONArray("optionList");

            // MySQL에 연결
            try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
                String insertSQL = "INSERT INTO deposite_products (fin_co_no, kor_co_nm, fin_prdt_cd, fin_prdt_nm, rsrv_type_nm, save_trm, intr_rate, intr_rate2) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                // PreparedStatement 사용하여 데이터 삽입
                for (int i = 0; i < baseList.length(); i++) {
                    JSONObject product = baseList.getJSONObject(i);
                    JSONObject productOptions = optionList.getJSONObject(i);  // optionList가 JSONArray가 아닌 JSONObject일 경우 처리

                        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                            System.out.println("======================================================================================");
                            System.out.println(product.getLong("fin_co_no"));
                            preparedStatement.setLong(1, product.getLong("fin_co_no"));
                            preparedStatement.setString(2, product.getString("kor_co_nm"));
                            preparedStatement.setString(3, product.getString("fin_prdt_cd"));  // 문자열로 처리
                            preparedStatement.setString(4, product.getString("fin_prdt_nm"));
                            preparedStatement.setString(5, product.getString("mtrt_int"));  // 없는 경우 기본값 0.0
                            // optionList에서 가져온 값
                            preparedStatement.setString(5, productOptions.getString("rsrv_type_nm"));
                            preparedStatement.setLong(6, productOptions.getLong("save_trm"));
                            preparedStatement.setDouble(7, productOptions.getDouble("intr_rate"));
                            preparedStatement.setDouble(8, productOptions.getDouble("intr_rate2"));
                            System.out.println("====================");
                            System.out.println(productOptions.getDouble("intr_rate"));


                            preparedStatement.executeUpdate();
                        }
                    }
                }

                return new ResponseEntity<>("Data saved successfully", HttpStatus.OK);

            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>("Error fetching data: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }