-- Файл scripts422_requestsForFillingDB.sql
-- Заполнение и тестирование базы cars_people_db

-- Добавляем машины (явно указываем все столбцы кроме ID)
INSERT INTO car (id, brand, model, price) VALUES
                                              (1, 'Toyota', 'Camry', 25000.00),
                                              (2, 'Honda', 'Civic', 22000.00),
                                              (3, 'BMW', 'X5', 60000.00),
                                              (4, 'Audi', 'A4', 35000.00),
                                              (5, 'Mercedes', 'C-Class', 42000.00);

-- Добавляем людей (явно указываем все столбцы кроме ID)
INSERT INTO person (id, name, age, has_license) VALUES
                                                    (1, 'Иван Иванов', 25, true),
                                                    (2, 'Мария Петрова', 30, true),
                                                    (3, 'Алексей Сидоров', 22, false),
                                                    (4, 'Елена Козлова', 28, true),
                                                    (5, 'Дмитрий Смирнов', 35, true);

-- Создаем связи: кто какой машиной пользуется
INSERT INTO person_car (person_id, car_id) VALUES
                                               (1, 1),  -- Иван пользуется Toyota Camry
                                               (1, 2),  -- Иван также пользуется Honda Civic
                                               (2, 1),  -- Мария пользуется Toyota Camry (такой же, как Иван)
                                               (2, 3),  -- Мария также пользуется BMW X5
                                               (3, 4),  -- Алексей пользуется Audi A4 (но без прав!)
                                               (4, 5),  -- Елена пользуется Mercedes C-Class
                                               (5, 2),  -- Дмитрий пользуется Honda Civic
                                               (5, 4);  -- Дмитрий также пользуется Audi A4

-- ТЕСТИРОВАНИЕ
SELECT '=== ВСЕ ЛЮДИ И ИХ МАШИНЫ ===' AS test_info;
SELECT p.name, p.age, p.has_license, c.brand, c.model, c.price
FROM person p
         LEFT JOIN person_car pc ON p.id = pc.person_id
         LEFT JOIN car c ON c.id = pc.car_id
ORDER BY p.name, c.brand;

SELECT '=== ЛЮДИ БЕЗ ПРАВ ===' AS test_info;
SELECT p.name, p.age, COUNT(pc.car_id) AS cars_used
FROM person p
         LEFT JOIN person_car pc ON p.id = pc.person_id
WHERE p.has_license = false
GROUP BY p.id, p.name, p.age;

SELECT '=== МАШИНЫ С НЕСКОЛЬКИМИ ВЛАДЕЛЬЦАМИ ===' AS test_info;

SELECT c.brand, c.model, c.price, COUNT(pc.person_id) AS users_count
FROM car c
         JOIN person_car pc ON c.id = pc.car_id
GROUP BY c.id, c.brand, c.model, c.price
HAVING COUNT(pc.person_id) > 1;