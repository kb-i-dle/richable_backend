-- create database coin;
-- create database test;
use product;
CREATE TABLE coin_list (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             coin_name VARCHAR(10) NOT NULL,
                             closing_price VARCHAR(50) NOT NULL,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
# CREATE TABLE coin (
#                       uid INT AUTO_INCREMENT PRIMARY KEY,
#                       currency VARCHAR(10) NOT NULL,
#                       balance DOUBLE NOT NULL,
#                       avg_buy_price DOUBLE NOT NULL,
#                       unit_currency VARCHAR(10) NOT NULL
# );
