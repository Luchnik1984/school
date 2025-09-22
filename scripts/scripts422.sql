-- Файл scripts422.sql
-- Создание таблиц для людей и машин со связью многие-ко-многим

-- Сначала удаляем таблицы если они существуют (для чистоты тестирования)
DROP TABLE IF EXISTS person_car;
DROP TABLE IF EXISTS person;
DROP TABLE IF EXISTS car;

-- Таблица машин
CREATE TABLE car (
                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                     brand VARCHAR(50) NOT NULL,
                     model VARCHAR(50) NOT NULL,
                     price NUMERIC(10, 2) NOT NULL CHECK (price > 0)
);

-- Таблица людей
CREATE TABLE person (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        age INTEGER NOT NULL CHECK (age >= 0),
                        has_license BOOLEAN NOT NULL
);

-- Таблица связи многие-ко-многим между людьми и машинами
CREATE TABLE person_car (
                            person_id BIGINT,
                            car_id BIGINT,
                            PRIMARY KEY (person_id, car_id),
                            FOREIGN KEY (person_id) REFERENCES person(id) ON DELETE CASCADE,
                            FOREIGN KEY (car_id) REFERENCES car(id) ON DELETE CASCADE
);

-- Создаем индексы для улучшения производительности
CREATE INDEX idx_person_name ON person(name);
CREATE INDEX idx_car_brand_model ON car(brand, model);
CREATE INDEX idx_person_car_person ON person_car(person_id);
CREATE INDEX idx_person_car_car ON person_car(car_id);

-- Проверка создания таблиц
SHOW TABLES;
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC';
