#create database test;
use product;
#drop table bond_data;
CREATE TABLE bond_list (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           crno VARCHAR(255),
                           scrsItmsKcd VARCHAR(255),
                           isinCd VARCHAR(255),
                           scrsItmsKcdNm VARCHAR(255),
                           bondIsurNm VARCHAR(255),
                           isinCdNm VARCHAR(255)
);