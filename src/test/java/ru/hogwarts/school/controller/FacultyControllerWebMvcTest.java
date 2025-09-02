package ru.hogwarts.school.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.dto.FacultyWithStudents;
import ru.hogwarts.school.dto.FacultyWithoutStudents;
import ru.hogwarts.school.mapper.FacultyMapper;
import ru.hogwarts.school.mapper.StudentMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultyController.class)
class FacultyControllerWebMvcTest {

    private final String testName = "testName";
    private final String testColor = "testColor";
    private final String testName2 = "testName2";
    private final String testColor2 = "testColor2";
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private FacultyService service;
    @MockitoBean
    private FacultyMapper facultyMapper;
    @MockitoBean
    private StudentMapper studentMapper;

    @Test
    void testAddFaculty() throws Exception {
        // Подготовка JSON
        JSONObject facultyJson = new JSONObject();
        facultyJson.put("name", testName);
        facultyJson.put("color", testColor);

        // Подготовка моков
        Faculty faculty = new Faculty();
        faculty.setName(testName);
        faculty.setColor(testColor);

        Faculty savedFaculty = new Faculty();
        savedFaculty.setId(1L);
        savedFaculty.setName(testName);
        savedFaculty.setColor(testColor);

        FacultyWithStudents facultyWithStudents = new FacultyWithStudents(1L, testName, testColor, List.of());

        Mockito.when(service.addFaculty(Mockito.any(Faculty.class))).thenReturn(savedFaculty);
        Mockito.when(facultyMapper.toFacultyWithStudents(savedFaculty)).thenReturn(facultyWithStudents);

        // Выполнение и проверка
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(facultyJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(testName))
                .andExpect(jsonPath("$.color").value(testColor));
    }

    @Test
    void testGetFacultyById() throws Exception {
        // Подготовка моков
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName(testName);
        faculty.setColor(testColor);

        FacultyWithStudents facultyWithStudents = new FacultyWithStudents(1L, testName, testColor, List.of());

        Mockito.when(service.getFaculty(1L)).thenReturn(faculty);
        Mockito.when(facultyMapper.toFacultyWithStudents(faculty)).thenReturn(facultyWithStudents);

        // Выполнение и проверка
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(testName))
                .andExpect(jsonPath("$.color").value(testColor));
    }

    @Test
    void testGetNonExistentFaculty() throws Exception {
        // Подготовка моков
        Mockito.when(service.getFaculty(999L)).thenReturn(null);

        // Выполнение и проверка
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateFaculty() throws Exception {
        // Подготовка JSON
        JSONObject facultyJson = new JSONObject();
        facultyJson.put("id", 1);
        facultyJson.put("name", "updateName");
        facultyJson.put("color", "updateColor");

        // Подготовка моков
        Faculty updatedFaculty = new Faculty();
        updatedFaculty.setId(1L);
        updatedFaculty.setName("updateName");
        updatedFaculty.setColor("updateColor");

        FacultyWithStudents facultyWithStudents = new FacultyWithStudents(
                1L, "updateName", "updateColor", List.of());

        Mockito.when(service.updateFaculty(Mockito.eq(1L), Mockito.any(Faculty.class))).thenReturn(updatedFaculty);
        Mockito.when(facultyMapper.toFacultyWithStudents(updatedFaculty)).thenReturn(facultyWithStudents);

        // Выполнение и проверка
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(facultyJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("updateName"))
                .andExpect(jsonPath("$.color").value("updateColor"));
    }

    @Test
    void testUpdateNonExistentFaculty() throws Exception {
        // Подготовка JSON
        JSONObject facultyJson = new JSONObject();
        facultyJson.put("id", 999);
        facultyJson.put("name", "Несуществующий");
        facultyJson.put("color", "черный");

        // Подготовка моков
        Mockito.when(service.updateFaculty(
                Mockito.eq(999L), Mockito.any(Faculty.class))).thenReturn(null);

        // Выполнение и проверка
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(facultyJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteFaculty() throws Exception {
        // Подготовка моков
        Mockito.doNothing().when(service).removeFaculty(1L);

        // Выполнение и проверка
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllFaculties() throws Exception {
        // Подготовка моков
        Faculty faculty1 = new Faculty();
        faculty1.setId(1L);
        faculty1.setName(testName);
        faculty1.setColor(testColor);

        Faculty faculty2 = new Faculty();
        faculty2.setId(2L);
        faculty2.setName(testName2);
        faculty2.setColor(testColor2);

        FacultyWithoutStudents facultyWithoutStudents1 = new FacultyWithoutStudents(1L, testName, testColor);
        FacultyWithoutStudents facultyWithoutStudents2 = new FacultyWithoutStudents(2L, testName2, testColor2);

        Mockito.when(service.getAllFaculties()).thenReturn(List.of(faculty1, faculty2));
        Mockito.when(facultyMapper.toFacultyWithoutStudents(faculty1)).thenReturn(facultyWithoutStudents1);
        Mockito.when(facultyMapper.toFacultyWithoutStudents(faculty2)).thenReturn(facultyWithoutStudents2);

        // Выполнение и проверка
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value(testName))
                .andExpect(jsonPath("$[0].color").value(testColor))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value(testName2))
                .andExpect(jsonPath("$[1].color").value(testColor2));
    }

    @Test
    void testGetFacultyByColor() throws Exception {
        // Подготовка моков
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName(testName);
        faculty.setColor(testColor);

        FacultyWithoutStudents facultyWithoutStudents = new FacultyWithoutStudents(1L, testName, testColor);

        Mockito.when(service.getFacultyByColor(testColor)).thenReturn(List.of(faculty));
        Mockito.when(facultyMapper.toFacultyWithoutStudents(faculty)).thenReturn(facultyWithoutStudents);

        // Выполнение и проверка
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/color/" + testColor)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value(testName))
                .andExpect(jsonPath("$[0].color").value(testColor));
    }

    @Test
    void testGetFacultyByColorNotFound() throws Exception {
        // Подготовка моков - пустой список
        Mockito.when(service.getFacultyByColor("несуществующий")).thenReturn(List.of());

        // Выполнение и проверка
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/color/несуществующий")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSearchFacultiesByNameOrColor() throws Exception {
        // Подготовка моков
        Faculty faculty1 = new Faculty();
        faculty1.setId(1L);
        faculty1.setName(testName);
        faculty1.setColor(testColor);

        Faculty faculty2 = new Faculty();
        faculty2.setId(2L);
        faculty2.setName(testName2);
        faculty2.setColor(testColor2);

        FacultyWithoutStudents facultyWithoutStudents1 = new FacultyWithoutStudents(1L, testName, testColor);
        FacultyWithoutStudents facultyWithoutStudents2 = new FacultyWithoutStudents(2L, testName2, testColor2);

        Mockito.when(service.getFacultiesByNameOrColor(testName)).thenReturn(List.of(faculty1));
        Mockito.when(facultyMapper.toFacultyWithoutStudents(faculty1)).thenReturn(facultyWithoutStudents1);

        // Выполнение и проверка
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/search")
                        .param("query", testName)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value(testName))
                .andExpect(jsonPath("$[0].color").value(testColor));
    }

    @Test
    void testSearchFacultiesByNameOrColorNotFound() throws Exception {
        // Подготовка моков - пустой список
        Mockito.when(service.getFacultiesByNameOrColor("несуществующий")).thenReturn(List.of());

        // Выполнение и проверка
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/search")
                        .param("query", "несуществующий")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetStudentsByFacultyId() throws Exception {
        // Подготовка моков - создаем студентов факультета
        ru.hogwarts.school.model.Student student1 = new ru.hogwarts.school.model.Student();
        student1.setId(1L);
        student1.setName("testStudent1");
        student1.setAge(11);

        ru.hogwarts.school.model.Student student2 = new ru.hogwarts.school.model.Student();
        student2.setId(2L);
        student2.setName("testStudent2");
        student2.setAge(11);

        ru.hogwarts.school.dto.StudentWithoutFaculty studentWithoutFaculty1 =
                new ru.hogwarts.school.dto.StudentWithoutFaculty(1L, "testStudent1");
        ru.hogwarts.school.dto.StudentWithoutFaculty studentWithoutFaculty2 =
                new ru.hogwarts.school.dto.StudentWithoutFaculty(2L, "testStudent2");

        // Настраиваем мок сервиса
        Mockito.when(service.getStudentsByFacultyId(1L)).thenReturn(List.of(student1, student2));

        // Настраиваем мок маппера студентов
        Mockito.when(studentMapper.toStudentWithoutFaculty(student1)).thenReturn(studentWithoutFaculty1);
        Mockito.when(studentMapper.toStudentWithoutFaculty(student2)).thenReturn(studentWithoutFaculty2);

        // Выполнение и проверка
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/1/students")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("testStudent1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("testStudent2"));
    }

    @Test
    void testGetStudentsByFacultyIdNotFound() throws Exception {
        // Подготовка моков - пустой список студентов
        Mockito.when(service.getStudentsByFacultyId(999L)).thenReturn(List.of());

        // Выполнение и проверка
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/999/students")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
