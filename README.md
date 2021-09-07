# README #

## Description

This is simple ATM implementation based on Spring (Security, Data, Web, Boot), 
MariaDB, FlyWay. Test base on Spring Test + Junit

## MariaDB configuration

1. Create a database for the service ```CREATE DATABASE `atm_base`;```

2. Create user ```CREATE USER 'atmuser' IDENTIFIED BY '<PASSWORD>';```

3. Allow access to the created database ```GRANT ALL privileges ON `atm_base`.* TO 'atmuser'@'%';```

4. Apply changes ```FLUSH PRIVILEGES;```

5. Check user access to the database ```SHOW GRANTS FOR 'quizuser';```

The same actions for test database