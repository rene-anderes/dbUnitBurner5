CREATE DATABASE IF NOT EXISTS `recipes`;
CREATE USER 'cookbook'@'localhost' IDENTIFIED BY 'cookbook';
GRANT ALL PRIVILEGES ON `recipes`.* TO 'cookbook'@'localhost';
