use test;
# drop table savings_products;
# drop table deposite_products;
CREATE TABLE deposite_list (
                                  ino BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  fin_co_no BIGINT,
                                  kor_co_nm  varchar(1000),
                                  fin_prdt_cd varchar(1000),
                                  fin_prdt_nm varchar(1000),
                                  rsrv_type_nm varchar(1000),
                                  save_trm int,
                                  intr_rate double,
                                  intr_rate2 double
);