-- Файл scripts.sql
-- SQL-запросы для работы со студентами (Шаг 3)

-- 1. Получить всех студентов, возраст которых между 16 и 18
SELECT *
FROM student
WHERE age BETWEEN 15 AND 17;

-- 2. Получить всех студентов, но отобразить только имена
SELECT name
FROM student;

-- 3. Получить всех студентов, у которых в имени есть буква 'e' (без учёта регистра)
SELECT *
FROM student
WHERE name ILIKE '%e%';

-- 4. Получить всех студентов, у которых возраст меньше идентификатора
SELECT *
FROM student
WHERE age < id;

-- 5. Получить всех студентов, упорядоченных по возрасту (по возрастанию)
SELECT *
FROM student
ORDER BY age;

-- Для сортировки по убыванию используйте:
-- SELECT * FROM student ORDER BY age DESC;