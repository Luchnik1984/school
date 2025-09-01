package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import ru.hogwarts.school.dto.FacultyWithStudents;
import ru.hogwarts.school.dto.FacultyWithoutStudents;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FacultyControllerIntegrationTest {

    @LocalServerPort
    private int port;
    private String baseUrl;

    private final String testName = "testFaculty";
    private final String testColor = "testColor";
    private final String updatedName = "updatedFaculty";
    private final String updatedColor = "updatedColor";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FacultyRepository facultyRepository;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/faculty";
        facultyRepository.deleteAll(); // Очищаем базу перед каждым тестом
    }

    @Test
    void testAddFaculty() {
        // ПРОВЕРКА: База пуста перед тестом
        assertTrue(facultyRepository.findAll().isEmpty(), "База должна быть пустой перед тестом");

        // Подготовка данных
        Faculty faculty = new Faculty();
        faculty.setName(testName);
        faculty.setColor(testColor);

        // Выполнение запроса
        ResponseEntity<FacultyWithStudents> response = restTemplate.postForEntity(
                baseUrl,
                faculty,
                FacultyWithStudents.class
        );

        // ПРОВЕРКА ответа от контроллера
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP статус должен быть 200 OK");
        assertNotNull(response.getBody(), "Тело ответа не должно быть null");
        assertEquals(testName, response.getBody().name(), "Название в ответе должно совпадать");
        assertEquals(testColor, response.getBody().color(), "Цвет в ответе должно совпадать");

        // ПРОВЕРКА: Данные сохранились в базу
        Faculty facultyFromDB = facultyRepository.findById(response.getBody().id())
                .orElse(null);
        assertNotNull(facultyFromDB, "Факультет должен быть найден в базе данных");
        assertEquals(testName, facultyFromDB.getName(), "Название в базе должно совпадать");
        assertEquals(testColor, facultyFromDB.getColor(), "Цвет в базе должно совпадать");
        assertEquals(1, facultyRepository.count(), "В базе должна быть ровно одна запись");
    }

    @Test
    void testGetFacultyById() {
        // ПРОВЕРКА: База пуста перед тестом
        assertTrue(facultyRepository.findAll().isEmpty(), "База должна быть пустой перед тестом");

        // ПОДГОТОВКА: Добавляем факультет через репозиторий
        Faculty faculty = new Faculty();
        faculty.setName(testName);
        faculty.setColor(testColor);
        Faculty savedFaculty = facultyRepository.save(faculty);

        // ПРОВЕРКА: Факультет добавлен в базу
        assertEquals(1, facultyRepository.count(), "В базе должна быть одна запись");

        // Выполнение запроса
        ResponseEntity<FacultyWithStudents> response = restTemplate.getForEntity(
                baseUrl + "/" + savedFaculty.getId(),
                FacultyWithStudents.class
        );

        // ПРОВЕРКА ответа
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP статус должен быть 200 OK");
        assertNotNull(response.getBody(), "Тело ответа не должно быть null");
        assertEquals(savedFaculty.getId(), response.getBody().id(), "ID должен совпадать");
        assertEquals(testName, response.getBody().name(), "Название должно совпадать");
        assertEquals(testColor, response.getBody().color(), "Цвет должен совпадать");
    }

    @Test
    void testGetNonExistentFaculty() {
        // ПРОВЕРКА: База пуста
        assertTrue(facultyRepository.findAll().isEmpty(), "База должна быть пустой");

        ResponseEntity<FacultyWithStudents> response = restTemplate.getForEntity(
                baseUrl + "/999",
                FacultyWithStudents.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Должен вернуть 404 для несуществующего факультета");
    }

    @Test
    void testUpdateFaculty() {
        // ПРОВЕРКА: База пуста
        assertTrue(facultyRepository.findAll().isEmpty(), "База должна быть пустой");

        // ПОДГОТОВКА: Добавляем факультет
        Faculty faculty = new Faculty();
        faculty.setName(testName);
        faculty.setColor(testColor);
        Faculty savedFaculty = facultyRepository.save(faculty);

        // ПРОВЕРКА: Факультет добавлен
        assertEquals(1, facultyRepository.count(), "В базе должна быть одна запись");

        // Подготавливаем обновленные данные
        Faculty updatedFaculty = new Faculty();
        updatedFaculty.setId(savedFaculty.getId());
        updatedFaculty.setName(updatedName);
        updatedFaculty.setColor(updatedColor);

        // Выполнение запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Faculty> request = new HttpEntity<>(updatedFaculty, headers);

        ResponseEntity<FacultyWithStudents> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.PUT,
                request,
                FacultyWithStudents.class
        );

        // ПРОВЕРКА ответа
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP статус должен быть 200 OK");
        assertNotNull(response.getBody(), "Тело ответа не должно быть null");
        assertEquals(updatedName, response.getBody().name(), "Название должно быть обновлено");
        assertEquals(updatedColor, response.getBody().color(), "Цвет должен быть обновлен");

        // ПРОВЕРКА: Данные обновились в базе
        Faculty facultyFromDB = facultyRepository.findById(savedFaculty.getId())
                .orElse(null);
        assertNotNull(facultyFromDB, "Факультет должен быть найден в базе");
        assertEquals(updatedName, facultyFromDB.getName(), "Название в базе должно быть обновлено");
        assertEquals(updatedColor, facultyFromDB.getColor(), "Цвет в базе должен быть обновлен");
        assertEquals(1, facultyRepository.count(), "В базе должна остаться одна запись");
    }

    @Test
    void testDeleteFaculty() {
        // ПРОВЕРКА: База пуста
        assertTrue(facultyRepository.findAll().isEmpty(), "База должна быть пустой");

        // ПОДГОТОВКА: Добавляем факультет
        Faculty faculty = new Faculty();
        faculty.setName(testName);
        faculty.setColor(testColor);
        Faculty savedFaculty = facultyRepository.save(faculty);

        // ПРОВЕРКА: Факультет добавлен
        assertEquals(1, facultyRepository.count(), "В базе должна быть одна запись");

        // Выполнение запроса на удаление
        restTemplate.delete(baseUrl + "/" + savedFaculty.getId());

        // ПРОВЕРКА: Факультет удален из базы
        assertFalse(facultyRepository.existsById(savedFaculty.getId()), "Факультет должен быть удален из базы");
        assertEquals(0, facultyRepository.count(), "База должна быть пустой");

        // ПРОВЕРКА: Попытка получить удаленный факультет
        ResponseEntity<FacultyWithStudents> response = restTemplate.getForEntity(
                baseUrl + "/" + savedFaculty.getId(),
                FacultyWithStudents.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Должен вернуть 404 для удаленного факультета");
    }

    @Test
    void testGetAllFaculties() {
        // ПРОВЕРКА: База пуста перед тестом
        assertTrue(facultyRepository.findAll().isEmpty(), "База должна быть пустой");

        // ПОДГОТОВКА: Добавляем два факультета
        Faculty faculty1 = new Faculty();
        faculty1.setName("Гриффиндор");
        faculty1.setColor("красный");

        Faculty faculty2 = new Faculty();
        faculty2.setName("Слизерин");
        faculty2.setColor("зеленый");

        facultyRepository.save(faculty1);
        facultyRepository.save(faculty2);

        // ПРОВЕРКА: Факультеты добавлены в базу
        assertEquals(2, facultyRepository.count(), "В базе должно быть две записи");

        // Выполнение запроса
        ResponseEntity<FacultyWithoutStudents[]> response = restTemplate.getForEntity(
                baseUrl,
                FacultyWithoutStudents[].class
        );

        // ПРОВЕРКА ответа
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP статус должен быть 200 OK");
        assertNotNull(response.getBody(), "Тело ответа не должно быть null");
        assertEquals(2, response.getBody().length, "Должно вернуть два факультета");
    }

    @Test
    void testGetFacultyByColor() {
        // ПРОВЕРКА: База пуста перед тестом
        assertTrue(facultyRepository.findAll().isEmpty(), "База должна быть пустой");

        // ПОДГОТОВКА: Добавляем факультеты разных цветов
        Faculty faculty1 = new Faculty();
        faculty1.setName("testFaculty1");
        faculty1.setColor("красный");

        Faculty faculty2 = new Faculty();
        faculty2.setName("testFaculty2");
        faculty2.setColor("зеленый");

        Faculty faculty3 = new Faculty();
        faculty3.setName("testFaculty3");
        faculty3.setColor("синий");

        facultyRepository.save(faculty1);
        facultyRepository.save(faculty2);
        facultyRepository.save(faculty3);

        // ПРОВЕРКА: Факультеты добавлены
        assertEquals(3, facultyRepository.count(), "В базе должно быть три записи");

        // Выполнение запроса
        ResponseEntity<FacultyWithoutStudents[]> response = restTemplate.getForEntity(
                baseUrl + "/color/красный",
                FacultyWithoutStudents[].class
        );

        // ПРОВЕРКА ответа
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP статус должен быть 200 OK");
        assertNotNull(response.getBody(), "Тело ответа не должно быть null");
        assertEquals(1, response.getBody().length, "Должно вернуть один красный факультет");
        assertEquals("testFaculty1", response.getBody()[0].name(), "Название должно совпадать");
    }

    @Test
    void testSearchNonExistentFaculty() {
        // ПРОВЕРКА: База пуста
        assertTrue(facultyRepository.findAll().isEmpty(), "База должна быть пустой");

        ResponseEntity<FacultyWithoutStudents[]> response = restTemplate.getForEntity(
                baseUrl + "/search?query=несуществующий",
                FacultyWithoutStudents[].class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Должен вернуть 404 для несуществующего факультета");
    }
}
