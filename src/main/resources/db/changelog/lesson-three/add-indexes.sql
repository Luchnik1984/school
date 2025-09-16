--liquibase formatted sql
--changeset student:add-student-name-index

-- Индекс для поиска по имени студента
CREATE INDEX IF NOT EXISTS idx_student_name ON student(name);
--comment: Добавлен индекс для поиска студентов по имени

--changeset student:add-faculty-name-color-index
-- Составной индекс для поиска по названию и цвету факультета
CREATE INDEX IF NOT EXISTS idx_faculty_name_color ON faculty(name, color);
--comment: Добавлен составной индекс для поиска факультетов по названию и цвету