-- Файл scripts423.sql
-- JOIN-запросы для получения информации о студентах Хогвартса

-- Получить информацию обо всех студентах (имя, возраст) вместе с названиями факультетов
SELECT
    s.name AS student_name,
    s.age AS student_age,
    f.name AS faculty_name
FROM student s
LEFT JOIN faculty f ON s.faculty_id = f.id
ORDER BY s.name;

-- Получить только тех студентов, у которых есть аватарки
SELECT
    s.name AS student_name,
    s.age AS student_age,
    f.name AS faculty_name,
    a.file_path AS avatar_path
FROM student s
LEFT JOIN faculty f ON s.faculty_id = f.id
INNER JOIN avatars a ON s.id = a.student_id
ORDER BY s.name;