create database coin;

use test;
CREATE TABLE coin_prices (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             coin_name VARCHAR(10) NOT NULL,
                             closing_price VARCHAR(50) NOT NULL,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
