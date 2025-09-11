
-- Файл scripts421.sql
-- SQL-запросы для добавления ограничений к таблицам Student и Faculty

-- Возраст студента не может быть меньше 16 лет
ALTER TABLE student
    ADD CONSTRAINT age_chek
        CHECK ( age>=16 );

-- Имена студентов должны быть уникальными и не равны нулю
ALTER TABLE student
    ALTER COLUMN name SET NOT NULL ;

ALTER TABLE student
    ADD CONSTRAINT unique_name
        UNIQUE (name);

-- Пара "название - цвет факультета" должна быть уникальной
ALTER TABLE faculty
    ADD CONSTRAINT unique_name_color
        UNIQUE (name, color);

-- При создании студента без возраста автоматически присваивать 20 лет
ALTER TABLE student
    ALTER COLUMN age SET DEFAULT 20;