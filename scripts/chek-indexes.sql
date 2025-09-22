-- Проверка индексов
SELECT '=== ИНДЕКСЫ СТУДЕНТОВ ===' as check_info;
SELECT * FROM pg_indexes WHERE  tablename = 'student';

SELECT '=== ИНДЕКСЫ ФАКУЛЬТЕТОВ ===' as check_info;
SELECT * FROM pg_indexes WHERE tablename = 'faculty';

SELECT '=== ПРОВЕРКА ИСПОЛЬЗОВАНИЯ ИНДЕКСОВ ===' as check_info;
EXPLAIN SELECT * FROM student WHERE name = 'Test Student';
EXPLAIN SELECT * FROM faculty WHERE name = 'Test Faculty' AND color = 'Test color';