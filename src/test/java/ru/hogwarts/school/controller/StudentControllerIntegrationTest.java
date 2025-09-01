package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import ru.hogwarts.school.dto.FacultyWithoutStudents;
import ru.hogwarts.school.dto.StudentWithFaculty;
import ru.hogwarts.school.dto.StudentWithoutFaculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class StudentControllerIntegrationTest {
    @LocalServerPort
    private int port;
    private String baseUrl;

    private final String testName = "testName";
    private final int testAge = 11;
    private final String updatedName = "udatedName";
    private final int updatedAge = 7;

    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/students";
        studentRepository.deleteAll();
    }

    @Test
    void testAddStudent() {
        // ПРОВЕРКА: База пуста перед тестом
        Assertions.assertTrue(studentRepository.findAll().isEmpty(), "База должна быть пустой");

        // Подготовка данных
        Student student = new Student();
        student.setName(testName);
        student.setAge(testAge);

        // Выполнение запроса
        ResponseEntity<StudentWithFaculty> response = restTemplate.postForEntity(
                baseUrl,
                student,
                StudentWithFaculty.class
        );

        // ПРОВЕРКА ответа от контроллера
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP статус должен быть 200 OK");
        assertNotNull(response.getBody(), "Тело ответа не должно быть null");
        assertEquals(testName, response.getBody().name(), "Имя в ответе должно совпадать");
        assertEquals(testAge, response.getBody().age(), "Возраст в ответе должен совпадать");

        // ПРОВЕРКА: Данные сохранились в базу
        Student studentFromDB = studentRepository.findById(response.getBody().id()).orElse(null);
        assertNotNull(studentFromDB);
        assertEquals(testName, studentFromDB.getName());
        assertEquals(testAge, studentFromDB.getAge());
    }

    @Test
    void testGetStudentById() {
        // ПРОВЕРКА: База пуста перед тестом
        Assertions.assertTrue(studentRepository.findAll().isEmpty(), "База должна быть пустой");

        // Подготовка данных
        Student student = new Student();
        student.setName(testName);
        student.setAge(testAge);
        Student savedStudent = studentRepository.save(student);

        // ПРОВЕРКА: Студент добавлен в базу
        assertEquals(1, studentRepository.count(), "В базе должна быть одна запись");

        // Выполнение запроса
        ResponseEntity<StudentWithFaculty> response = restTemplate.getForEntity(
                baseUrl + "/" + savedStudent.getId(),
                StudentWithFaculty.class
        );

        // ПРОВЕРКА ответа
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP статус должен быть 200 OK");
        assertNotNull(response.getBody(), "Тело ответа не должно быть null");
        assertEquals(savedStudent.getId(), response.getBody().id(), "ID должен совпадать");
        assertEquals(testName, response.getBody().name(), "Имя должно совпадать");
        assertEquals(testAge, response.getBody().age(), "Возраст должен совпадать");
    }

    @Test
    void testGetNonExistentStudent() {
        // ПРОВЕРКА: База пуста перед тестом
        assertTrue(studentRepository.findAll().isEmpty(), "База должна быть пустой");

        ResponseEntity<StudentWithFaculty> response = restTemplate.getForEntity(
                baseUrl + "/999",
                StudentWithFaculty.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Должен вернуть 404 для несуществующего студента");
    }

    @Test
    void testUpdateStudent() {
        // ПРОВЕРКА: База пуста перед тестом
        Assertions.assertTrue(studentRepository.findAll().isEmpty(), "База должна быть пустой");

        // ПОДГОТОВКА: Добавляем студента
        Student student = new Student();
        student.setName(testName);
        student.setAge(testAge);
        Student savedStudent = studentRepository.save(student);

        // ПРОВЕРКА: Студент добавлен
        assertEquals(1, studentRepository.count(), "В базе должна быть одна запись");

        // Подготавливаем обновленные данные
        Student updatedStudent = new Student();
        updatedStudent.setId(savedStudent.getId());
        updatedStudent.setName(updatedName);
        updatedStudent.setAge(updatedAge);

        // Выполнение запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Student> request = new HttpEntity<>(updatedStudent, headers);

        ResponseEntity<StudentWithFaculty> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.PUT,
                request,
                StudentWithFaculty.class
        );

        // ПРОВЕРКА ответа
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP статус должен быть 200 OK");
        assertNotNull(response.getBody(), "Тело ответа не должно быть null");
        assertEquals(updatedName, response.getBody().name(), "Имя должно быть обновлено");
        assertEquals(updatedAge, response.getBody().age(), "Возраст должен быть обновлен");

        // ПРОВЕРКА: Данные обновились в базе
        Student studentFromDB = studentRepository.findById(savedStudent.getId())
                .orElse(null);
        assertNotNull(studentFromDB, "Студент должен быть найден в базе");
        assertEquals(updatedName, studentFromDB.getName(), "Имя в базе должно быть обновлено");
        assertEquals(updatedAge, studentFromDB.getAge(), "Возраст в базе должен быть обновлен");
        assertEquals(1, studentRepository.count(), "В базе должна остаться одна запись");
    }

    @Test
    void testDeleteStudent() {
        // ПРОВЕРКА: База пуста перед тестом
        Assertions.assertTrue(studentRepository.findAll().isEmpty(), "База должна быть пустой");

        // ПОДГОТОВКА: Добавляем студента
        Student student = new Student();
        student.setName(testName);
        student.setAge(testAge);
        Student savedStudent = studentRepository.save(student);

        // ПРОВЕРКА: Студент добавлен
        assertEquals(1, studentRepository.count(), "В базе должна быть одна запись");

        // Выполнение запроса на удаление
        restTemplate.delete(baseUrl + "/" + savedStudent.getId());

        // ПРОВЕРКА: Студент удален из базы
        assertFalse(studentRepository.existsById(savedStudent.getId()), "Студент должен быть удален из базы");
        assertEquals(0, studentRepository.count(), "База должна быть пустой");

        // ПРОВЕРКА: Попытка получить удаленного студента
        ResponseEntity<StudentWithFaculty> response = restTemplate.getForEntity(
                baseUrl + "/" + savedStudent.getId(),
                StudentWithFaculty.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Должен вернуть 404 для удаленного студента");
    }

    @Test
    void testGetAllStudents() {
        // ПРОВЕРКА: База пуста перед тестом
        assertTrue(studentRepository.findAll().isEmpty(), "База должна быть пустой");

        // ПОДГОТОВКА: Добавляем двух студентов
        Student student1 = new Student();
        student1.setName("Гермиона Грейнджер");
        student1.setAge(11);

        Student student2 = new Student();
        student2.setName("Рон Уизли");
        student2.setAge(11);

        studentRepository.save(student1);
        studentRepository.save(student2);

        // ПРОВЕРКА: Студенты добавлены в базу
        assertEquals(2, studentRepository.count(), "В базе должно быть две записи");

        // Выполнение запроса
        ResponseEntity<StudentWithoutFaculty[]> response = restTemplate.getForEntity(
                baseUrl,
                StudentWithoutFaculty[].class
        );

        // ПРОВЕРКА ответа
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP статус должен быть 200 OK");
        assertNotNull(response.getBody(), "Тело ответа не должно быть null");
        assertEquals(2, response.getBody().length, "Должно вернуть двух студентов");
    }

    @Test
    void testGetStudentByAge() {
        // ПРОВЕРКА: База пуста перед тестом
        assertTrue(studentRepository.findAll().isEmpty(), "База должна быть пустой");

        // ПОДГОТОВКА: Добавляем студентов разного возраста
        Student student1 = new Student();
        student1.setName("Студент 11 лет");
        student1.setAge(11);

        Student student2 = new Student();
        student2.setName("Студент 12 лет");
        student2.setAge(12);

        Student student3 = new Student();
        student3.setName("Еще один студент 11 лет");
        student3.setAge(11);

        studentRepository.save(student1);
        studentRepository.save(student2);
        studentRepository.save(student3);

        // ПРОВЕРКА: Студенты добавлены
        assertEquals(3, studentRepository.count(), "В базе должно быть три записи");

        // Выполнение запроса
        ResponseEntity<StudentWithoutFaculty[]> response = restTemplate.getForEntity(
                baseUrl + "/age/11",
                StudentWithoutFaculty[].class
        );

        // ПРОВЕРКА ответа
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP статус должен быть 200 OK");
        assertNotNull(response.getBody(), "Тело ответа не должно быть null");
        assertEquals(2, response.getBody().length, "Должно вернуть двух студентов 11 лет");
    }

    @Test
    void testGetFacultyByStudentId() {
        // ПРОВЕРКА: База пуста перед тестом
        assertTrue(studentRepository.findAll().isEmpty(), "База должна быть пустой");

        // ПОДГОТОВКА: Добавляем студента (без факультета)
        Student student = new Student();
        student.setName(testName);
        student.setAge(testAge);
        Student savedStudent = studentRepository.save(student);

        // ПРОВЕРКА: Студент добавлен
        assertEquals(1, studentRepository.count(), "В базе должна быть одна запись");

        // Выполнение запроса
        ResponseEntity<FacultyWithoutStudents> response = restTemplate.getForEntity(
                baseUrl + "/" + savedStudent.getId() + "/faculty",
                FacultyWithoutStudents.class
        );

        // ПРОВЕРКА: Для студента без факультета должен вернуть 404
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Должен вернуть 404 для студента без факультета");
    }

}
