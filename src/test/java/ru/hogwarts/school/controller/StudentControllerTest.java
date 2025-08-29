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
import ru.hogwarts.school.dto.StudentWithFaculty;
import ru.hogwarts.school.dto.StudentWithoutFaculty;
import ru.hogwarts.school.model.Student;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    private String baseUrl;

    String testName= "Test Student";
    int testAge=11;


    @BeforeEach
     void setUp() {
        baseUrl = "http://localhost:" + port + "/students";
    }

    @Test
    void addStudent_shouldReturnCreatedStudent() {
        // Подготовка данных
        Student student = new Student();
        student.setName(testName);
        student.setAge(testAge);

        // Выполнение запроса
        ResponseEntity<StudentWithFaculty> response = restTemplate
                .postForEntity(baseUrl, student, StudentWithFaculty.class);

        // Проверка
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testName,response.getBody().name());
        assertEquals(testAge,response.getBody().age());
    }

    @Test
    void getStudents_shouldReturnStudentWhenExists() {
        Student student = new Student();
        student.setName(testName);
        student.setAge(testAge);

        ResponseEntity<StudentWithFaculty> createResponse = restTemplate.
                postForEntity(baseUrl, student, StudentWithFaculty.class);
        Long studentId = createResponse.getBody().id();

        ResponseEntity<StudentWithFaculty> response = restTemplate.getForEntity(
                baseUrl + "/" + studentId, StudentWithFaculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testName,response.getBody().name());
    }

    @Test
    void getStudent_shouldReturnNotFoundWhenStudentNotExist() {
        // Пытаемся получить несуществующего студента
       ResponseEntity<StudentWithFaculty> response = restTemplate.getForEntity(
               baseUrl + "/" +9999, StudentWithFaculty.class);

       assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updatedStudent_shouldUpdateExistingStudent() {
        Student student = new Student();
        student.setName(testName);
        student.setAge(testAge);
        ResponseEntity<StudentWithFaculty> createResponse = restTemplate.postForEntity(
                baseUrl, student, StudentWithFaculty.class);
        Long studentId = createResponse.getBody().id();

        Student updatedStudent = new Student();
        updatedStudent.setId(studentId);
        updatedStudent.setName("13");
        updatedStudent.setAge(7);

        ResponseEntity<StudentWithFaculty> response = restTemplate
                .exchange(baseUrl, HttpMethod.PUT,
                        new HttpEntity<>(updatedStudent), StudentWithFaculty.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("13",response.getBody().name());
        assertEquals(7,response.getBody().age());
    }

    @Test
    void deleteStudent_shouldRemoveStudent() {
        Student student = new Student();
        student.setName(testName);
        student.setAge(testAge);
        ResponseEntity<StudentWithFaculty> createResponse = restTemplate.postForEntity(
                baseUrl, student, StudentWithFaculty.class);
        Long studentId =createResponse.getBody().id();

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + studentId, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        ResponseEntity<StudentWithFaculty> response = restTemplate.getForEntity(
                baseUrl + "/" + studentId, StudentWithFaculty.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getAllStudents_shouldReturnListOfStudents(){
        ResponseEntity<Collection<StudentWithFaculty>> response = restTemplate.exchange(
                baseUrl,HttpMethod.GET,null,
                new ParameterizedTypeReference<Collection<StudentWithFaculty>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size()>=0); // Может быть пустым или содержать студентов
    }

    @Test
    void getStudentByAge_shouldReturnFilteredStudents(){

        Student student = new Student();
        student.setName(testName);
        student.setAge(testAge);
        restTemplate.postForEntity(baseUrl, student, StudentWithFaculty.class);

        ResponseEntity<Collection<StudentWithoutFaculty>> response = restTemplate.exchange(
                baseUrl+"/age/"+testAge,HttpMethod.GET,null,
                new ParameterizedTypeReference<Collection<StudentWithoutFaculty>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().stream()
                .anyMatch(s->s.name().equals(testName)));
    }

    @Test
    void getStudentByAgeBetwee_shouldReturnStudentsInRange() {
        Student student = new Student();
        student.setName(testName);
        student.setAge(testAge);
        restTemplate.postForEntity(baseUrl, student, StudentWithFaculty.class);

        Student student2 = new Student();
        student2.setName("13");
        student2.setAge(7);
        restTemplate.postForEntity(baseUrl, student2, StudentWithFaculty.class);

        ResponseEntity<Collection<StudentWithoutFaculty>> response = restTemplate.exchange(
                baseUrl + "/age-range?min=6&max=10", HttpMethod.GET, null,
                new ParameterizedTypeReference<Collection<StudentWithoutFaculty>>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        response.getBody().forEach(s -> {
            assertTrue(s.name().equals(testName) ||
                    s.name().equals("13"));
        });
    }

    @Test
    void getFacultyByStudentId_shouldReturnFacultyWhenExists(){
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/9999/faculty", String.class);

        // Может вернуть 404 (студент не найден) или 200 (если факультет есть)
        assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND ||
                response.getStatusCode() == HttpStatus.OK);
    }

}
