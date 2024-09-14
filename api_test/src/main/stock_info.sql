use product;
# drop table stock_info;
CREATE TABLE stock_list
(
    `index`       INT AUTO_INCREMENT PRIMARY KEY, -- Auto-incrementing primary key
    standard_code VARCHAR(50),                    -- 표준코드 (standard code)
    short_code    VARCHAR(50),                    -- 단축코드 (short code)
    kr_stock_nm   VARCHAR(255),                   -- 한글 종목명 (Korean stock name)
    kr_stock_abbr VARCHAR(100),                   -- 한글 종목약명 (Korean stock abbreviation)
    market        varchar(100),
    price         INT

);

SHOW VARIABLES LIKE 'secure_file_priv';
# 위의 경로로 파일 이동

SET GLOBAL local_infile = 1;
SHOW VARIABLES LIKE 'local_infile';

LOAD DATA INFILE 'C:\\ProgramData\\MySQL\\MySQL Server 8.2\\Uploads\\stock_data.csv' #파일을 여기에 옮겨야 함
    INTO TABLE stock_list
    FIELDS TERMINATED BY ','
    ENCLOSED BY '"'
    LINES TERMINATED BY '\n'
    IGNORE 1 LINES
    (`index`, standard_code, short_code, kr_stock_nm, kr_stock_abbr,market,price);
