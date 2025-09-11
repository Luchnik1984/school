-- Файл scripts422.sql
-- Создание таблиц для людей и машин с связью многие-ко-многим

-- Сначала удаляем таблицы если они существуют (для чистоты тестирования)
DROP TABLE IF EXISTS person_car;
DROP TABLE IF EXISTS person;
DROP TABLE IF EXISTS car;

-- Таблица машин
CREATE TABLE car(
    id BIGSERIAL PRIMARY KEY
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    price NUMERIC(10,2) NOT NULL CHEK (price>0)
);

-- Таблица людей
CREATE TABLE person(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INTEGER NOT NULL CHECK ( age>=0 ),
    has_license BOOLEAN NOT NULL DEFAULT FALSE
);

-- Таблица связи многие-ко-многим между людьми и машинами
CREATE TABLE person_car(
    person_id BIGINT REFERENCES person(id) ON CASCADE,
    car_id BIGINT REFERENCES car(id) ON DELETE CASCADE,
    PRIMARY KEY (person_id, car_id)
);

-- Добавляем комментарии к таблицам для лучшего понимания
COMMENT ON TABLE car IS 'Таблица автомобилей';
COMMENT ON TABLE person IS 'Таблица людей';
COMMENT ON TABLE person_car IS 'Таблица связи многие-ко-многим между людьми и автомобилями';

-- Создаем индексы для улучшения производительности
CREATE INDEX idx_person_name ON person(name);
CREATE INDEX idx_car_brand_model ON car(brand, model);
CREATE INDEX idx_person_car_person ON person_car(person_id);
CREATE INDEX idx_person_car_car ON person_car(car_id);