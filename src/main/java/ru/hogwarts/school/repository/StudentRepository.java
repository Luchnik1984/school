package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Collection<Student> findByAge(int age);

    Collection<Student> findByAgeBetween(int minAge, int maxAge);

    List<Student> findByFacultyId(Long facultyId);

    // Получение количества всех студентов
    @Query ("SELECT COUNT(s) FROM Student s")
    int countAllStudents();

    // Получение среднего возраста студентов
    @Query("SELECT AVG (s.age) FROM Student s")
    Double findAverageAge();

    // Получение 5 последних студентов (с наибольшими ID)
    @Query ("SELECT s FROM Student s ORDER BY s.id DESC LIMIT 5")
    List<Student> findLastFiveStudents();




}
