package org.scoula.config;

import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import javax.sql.DataSource;

@Configuration
@PropertySource({"classpath:/application.properties"})
public class DatabaseConfig {

    // user_infoDB 설정 ====================================================================
    @Value("${user.jdbc.driver}")
    private String user_driver;
    @Value("${user.jdbc.url}")
    private String user_url;
    @Value("${user.jdbc.username}")
    private String user_user;
    @Value("${user.jdbc.password}")
    private String user_pw;

    @Bean(name = "user_infoDB")
    public DataSource userInfoDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(user_driver);
        dataSource.setUrl(user_url);
        dataSource.setUsername(user_user);
        dataSource.setPassword(user_pw);
        return dataSource;
    }

    @Bean(name = "userJdbcTemplate")
    public JdbcTemplate userJdbcTemplate(@Qualifier("user_infoDB") DataSource user_info_DataSource) {
        return new JdbcTemplate(user_info_DataSource);
    }

    // assetDB 설정======================================================================
    @Value("${asset.jdbc.driver}")
    private String asset_driver;
    @Value("${asset.jdbc.url}")
    private String asset_url;
    @Value("${asset.jdbc.username}")
    private String asset_user;
    @Value("${asset.jdbc.password}")
    private String asset_pw;

    @Bean(name = "assetDB")
    public DataSource assetDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(asset_driver);
        dataSource.setUrl(asset_url);
        dataSource.setUsername(asset_user);
        dataSource.setPassword(asset_pw);
        return dataSource;
    }

    @Bean(name = "assetJdbcTemplate")
    public JdbcTemplate assetJdbcTemplate(@Qualifier("assetDB") DataSource assetDataSource) {
        return new JdbcTemplate(assetDataSource);
    }

    // productDB 설정 ===============================================================
    @Value("${product.jdbc.driver}")
    private String product_driver;
    @Value("${product.jdbc.url}")
    private String product_url;
    @Value("${product.jdbc.username}")
    private String product_user;
    @Value("${product.jdbc.password}")
    private String product_pw;

    @Bean(name = "productDB")
    public DataSource productDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(product_driver);
        dataSource.setUrl(product_url);
        dataSource.setUsername(product_user);
        dataSource.setPassword(product_pw);
        return dataSource;
    }

    @Bean(name = "productJdbcTemplate")
    public JdbcTemplate productJdbcTemplate(@Qualifier("productDB") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    // outcomeDB 설정 =======================================================
    @Value("${outcome.jdbc.driver}")
    private String outcome_driver;
    @Value("${outcome.jdbc.url}")
    private String outcome_url;
    @Value("${outcome.jdbc.username}")
    private String outcome_user;
    @Value("${outcome.jdbc.password}")
    private String outcome_pw;

    @Bean(name = "outcomeDB")
    public DataSource outcomeDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(outcome_driver);
        dataSource.setUrl(outcome_url);
        dataSource.setUsername(outcome_user);
        dataSource.setPassword(outcome_pw);
        return dataSource;
    }

    @Bean(name = "outcomeJdbcTemplate")
    public JdbcTemplate outcomeJdbcTemplate(@Qualifier("outcomeDB") DataSource outcomeDataSource) {
        return new JdbcTemplate(outcomeDataSource);
    }

    // boardDB 설정 ==============================================================
    @Value("${board.jdbc.driver}")
    private String board_driver;
    @Value("${board.jdbc.url}")
    private String board_url;
    @Value("${board.jdbc.username}")
    private String board_user;
    @Value("${board.jdbc.password}")
    private String board_pw;

    @Bean(name = "boardDB")
    public DataSource boardDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(board_driver);
        dataSource.setUrl(board_url);
        dataSource.setUsername(board_user);
        dataSource.setPassword(board_pw);
        return dataSource;
    }

    @Bean(name = "boardJdbcTemplate")
    public JdbcTemplate boardJdbcTemplate(@Qualifier("boardDB") DataSource boardDataSource) {
        return new JdbcTemplate(boardDataSource);
    }


}
