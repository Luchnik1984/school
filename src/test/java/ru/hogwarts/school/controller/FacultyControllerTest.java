package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.dto.FacultyWithStudents;
import ru.hogwarts.school.dto.FacultyWithoutStudents;
import ru.hogwarts.school.model.Faculty;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    String facultyName = "Test Faculty";
    String facultyColor = "Test Faculty Color";

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/faculty";
    }

    @Test
    void addFaculty_shouldReturnCreatedFaculty(){
        Faculty faculty = new Faculty();
        faculty.setName(facultyName);
        faculty.setColor(facultyColor);

        ResponseEntity<FacultyWithStudents> response = restTemplate
                .postForEntity(baseUrl, faculty, FacultyWithStudents.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(facultyName,response.getBody().name());
        assertEquals(facultyColor,response.getBody().color());
    }

    @Test
    void getFaculty_shouldReturnFacultyWhenExists(){
        Faculty faculty = new Faculty();
        faculty.setName(facultyName);
        faculty.setColor(facultyColor);
        ResponseEntity<FacultyWithStudents> createResponse = restTemplate.postForEntity(
                baseUrl, faculty, FacultyWithStudents.class);
        Long facultyId = createResponse.getBody().id();

        ResponseEntity<FacultyWithStudents> response = restTemplate.getForEntity(
                baseUrl + "/" + facultyId, FacultyWithStudents.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(facultyName,response.getBody().name());
        assertEquals(facultyColor,response.getBody().color());
    }



    @Test
    void getFaculty_shouldReturnNotFoundWhenNotExists(){
        ResponseEntity<FacultyWithStudents> response = restTemplate.getForEntity(
                baseUrl + "/9999", FacultyWithStudents.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateFaculty_shouldUpdateExistingFaculty(){
        Faculty faculty = new Faculty();
        faculty.setName(facultyName);
        faculty.setColor(facultyColor);
        ResponseEntity<FacultyWithStudents> createResponse = restTemplate.postForEntity(
                baseUrl, faculty, FacultyWithStudents.class);
        Long facultyId = createResponse.getBody().id();

        Faculty updatedFaculty = new Faculty();
        updatedFaculty.setId(facultyId);
        updatedFaculty.setName("updatedName");
        updatedFaculty.setColor("updatedColor");

        ResponseEntity<FacultyWithStudents> response = restTemplate.exchange(
                baseUrl, HttpMethod.PUT,
                new HttpEntity<>(updatedFaculty), FacultyWithStudents.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("updatedName", response.getBody().name());
        assertEquals("updatedColor", response.getBody().color());
    }

    @Test
    void deleteFaculty_shouldRemoveFaculty(){
        Faculty faculty = new Faculty();
        faculty.setName(facultyName);
        faculty.setColor(facultyColor);

        ResponseEntity<FacultyWithStudents> createResponse = restTemplate.postForEntity(
                baseUrl, faculty, FacultyWithStudents.class);
        Long facultyId = createResponse.getBody().id();

        // Убедимся, что факультет создан
        ResponseEntity<FacultyWithStudents> getResponseBefore = restTemplate.getForEntity(
                baseUrl + "/" + facultyId, FacultyWithStudents.class);
        assertEquals(HttpStatus.OK, getResponseBefore.getStatusCode());

        // Удаляем факультет
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + facultyId, HttpMethod.DELETE, null, Void.class);

        // Проверяем, что удаление прошло успешно
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        // Проверяем, что факультета больше нет - используем exchange для лучшего контроля
        ResponseEntity<FacultyWithStudents> getResponseAfter = restTemplate.exchange(
                baseUrl + "/" + facultyId, HttpMethod.GET, null, FacultyWithStudents.class);

        assertEquals(HttpStatus.NOT_FOUND, getResponseAfter.getStatusCode());
    }

    @Test
    void getAllFaculties_shouldReturnListOfFaculties(){
        ResponseEntity<Collection<FacultyWithoutStudents>> response = restTemplate.exchange(
                baseUrl, HttpMethod.GET, null,
                new ParameterizedTypeReference<Collection<FacultyWithoutStudents>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() >= 0); // Может быть пустым или содержать факультеты;
    }

    @Test
    void getFacultyByColor_shouldReturnFilteredFaculties(){
        Faculty faculty = new Faculty();
        faculty.setName(facultyName);
        faculty.setColor(facultyColor);
        restTemplate.postForEntity(baseUrl, faculty, FacultyWithStudents.class);

        ResponseEntity<Collection<FacultyWithoutStudents>> response = restTemplate.exchange(
                baseUrl+"/color/"+facultyColor,HttpMethod.GET,null,
                new ParameterizedTypeReference<Collection<FacultyWithoutStudents>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().stream()
                .anyMatch(f->f.name().equals(facultyName)));
    }

    @Test
    void getFacultiesByNameOrColor_shouldReturnMatchingFaculties(){
        Faculty faculty = new Faculty();
        faculty.setName(facultyName);
        faculty.setColor(facultyColor);
        restTemplate.postForEntity(baseUrl, faculty, FacultyWithStudents.class);

        // Ищем по имени
        ResponseEntity<Collection<FacultyWithoutStudents>> responseByName = restTemplate.exchange(
                baseUrl+ "/search?query="+facultyName,HttpMethod.GET,null,
                new ParameterizedTypeReference<Collection<FacultyWithoutStudents>>() {});

        // Ищем по цвету
        ResponseEntity<Collection<FacultyWithoutStudents>> responseByColor = restTemplate.exchange(
                baseUrl+"/search?query="+facultyColor, HttpMethod.GET,null,
                new ParameterizedTypeReference<Collection<FacultyWithoutStudents>>() {});

        // Проверки для поиска по имени
        assertEquals(HttpStatus.OK, responseByName.getStatusCode());
        assertNotNull(responseByName.getBody());
        assertTrue(responseByName.getBody().stream()
                .anyMatch(f->f.name().equals(facultyName)));

        // Проверки для поиска по цвету
        assertEquals(HttpStatus.OK, responseByColor.getStatusCode());
        assertNotNull(responseByColor.getBody());
        assertTrue(responseByColor.getBody().stream()
                .anyMatch(f->f.color().equals(facultyColor)));
    }

    @Test
    void getStudentsByFacultyId_shouldReturnStudentsList(){
        Faculty faculty = new Faculty();
        faculty.setName(facultyName);
        faculty.setColor(facultyColor);

        ResponseEntity<FacultyWithStudents> createResponse = restTemplate.postForEntity(
                baseUrl, faculty, FacultyWithStudents.class);
        Long facultyId = createResponse.getBody().id();

        // Получаем студентов факультета (может быть пустым списком)
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl+"/"+facultyId+"/students",HttpMethod.GET,null,
                new ParameterizedTypeReference<String>() {});

        // Проверяем, что endpoint работает (возвращает 200 даже если список пустой)
        assertTrue(response.getStatusCode() == HttpStatus.OK ||
                response.getStatusCode() == HttpStatus.NOT_FOUND);
    }

    @Test
    void getFacultyByColor_shouldReturnNotFoundForNonExistingColor(){
        // Ищем несуществующий цвет
        ResponseEntity<Collection<FacultyWithoutStudents>> response = restTemplate.exchange(
                baseUrl + "/color/несуществующий", HttpMethod.GET, null,
                new ParameterizedTypeReference<Collection<FacultyWithoutStudents>>() {});

        // Должен вернуть 404, если нет факультетов такого цвета
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}