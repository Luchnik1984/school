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
import ru.hogwarts.school.dto.FacultyWithoutStudents;
import ru.hogwarts.school.dto.StudentWithFaculty;
import ru.hogwarts.school.dto.StudentWithoutFaculty;
import ru.hogwarts.school.mapper.FacultyMapper;
import ru.hogwarts.school.mapper.StudentMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService service;

    @MockitoBean
    private StudentMapper mapper;

    @MockitoBean
    private FacultyMapper facultyMapper;

    private final String testName = "testName";
    private final int testAge = 11;

    @Test
    void testAddStudent() throws Exception {
        // Подготовка JSON
        JSONObject studentJson = new JSONObject();
        studentJson.put("name", testName);
        studentJson.put("age", testAge);

        // Подготовка моков
        Student student = new Student();
        student.setName(testName);
        student.setAge(testAge);

        Student savedStudent = new Student();
        savedStudent.setId(1L);
        savedStudent.setName(testName);
        savedStudent.setAge(testAge);

        StudentWithFaculty studentWithFaculty = new StudentWithFaculty(1L, testName, testAge, null);

        Mockito.when(service.addStudent(Mockito.any(Student.class))).thenReturn(savedStudent);
        Mockito.when(mapper.toStudentWithFaculty(savedStudent)).thenReturn(studentWithFaculty);

        // Выполнение и проверка
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/students")
                        .content(studentJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(testName))
                .andExpect(jsonPath("$.age").value(testAge));
    }

    @Test
    void testGetStudentById() throws Exception {
        // Подготовка моков
        Student student = new Student();
        student.setId(1L);
        student.setName(testName);
        student.setAge(testAge);

        StudentWithFaculty studentWithFaculty = new StudentWithFaculty(1L, testName, testAge, null);

        Mockito.when(service.getStudent(1L)).thenReturn(student);
        Mockito.when(mapper.toStudentWithFaculty(student)).thenReturn(studentWithFaculty);

        // Выполнение и проверка
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/students/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(testName))
                .andExpect(jsonPath("$.age").value(testAge));
    }

    @Test
    void testGetNonExistentStudent() throws Exception {
        // Подготовка моков
        Mockito.when(service.getStudent(999L)).thenReturn(null);

        // Выполнение и проверка
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/students/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateStudent() throws Exception {
        // Подготовка JSON
        JSONObject studentJson = new JSONObject();
        studentJson.put("id", 1);
        studentJson.put("name", "updatedName");
        studentJson.put("age", 12);

        // Подготовка моков
        Student updatedStudent = new Student();
        updatedStudent.setId(1L);
        updatedStudent.setName("updatedName");
        updatedStudent.setAge(12);

        StudentWithFaculty studentWithFaculty = new StudentWithFaculty(1L, "updatedName", 12, null);

        Mockito.when(service.updateStudent(Mockito.eq(1L), Mockito.any(Student.class))).thenReturn(updatedStudent);
        Mockito.when(mapper.toStudentWithFaculty(updatedStudent)).thenReturn(studentWithFaculty);

        // Выполнение и проверка
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/students")
                        .content(studentJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("updatedName"))
                .andExpect(jsonPath("$.age").value(12));
    }

    @Test
    void testUpdateNonExistentStudent() throws Exception {
        // Подготовка JSON
        JSONObject studentJson = new JSONObject();
        studentJson.put("id", 999);
        studentJson.put("name", "nonExistent");
        studentJson.put("age", 12);

        // Подготовка моков
        Mockito.when(service.updateStudent(Mockito.eq(999L), Mockito.any(Student.class))).thenReturn(null);

        // Выполнение и проверка
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/students")
                        .content(studentJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteStudent() throws Exception {
        // Подготовка моков - для delete обычно не возвращает значение, просто проверяем что не падает
        Mockito.doNothing().when(service).removeStudent(1L);

        // Выполнение и проверка
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/students/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllStudents() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/students")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetStudentByAge() throws Exception {
        // Подготовка моков - создаем студента нужного возраста
        Student student = new Student();
        student.setId(1L);
        student.setName(testName);
        student.setAge(11);

        StudentWithoutFaculty studentWithoutFaculty = new StudentWithoutFaculty(1L, testName);

        // Настраиваем мок сервиса чтобы возвращал студента
        Mockito.when(service.getStudentByAge(11)).thenReturn(java.util.List.of(student));
        Mockito.when(mapper.toStudentWithoutFaculty(student)).thenReturn(studentWithoutFaculty);

        // Выполнение и проверка
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/students/age/11")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value(testName));
    }

    @Test
    void testGetStudentByAgeNotFound() throws Exception {
        // Подготовка моков - пустой список студентов
        Mockito.when(service.getStudentByAge(99)).thenReturn(java.util.List.of());

        // Выполнение и проверка - должен вернуть 404
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/students/age/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetStudentByAgeRange() throws Exception {
        // Подготовка моков - создаем студентов в диапазоне
        Student student1 = new Student();
        student1.setId(1L);
        student1.setName("Студент 1");
        student1.setAge(11);

        Student student2 = new Student();
        student2.setId(2L);
        student2.setName("Студент 2");
        student2.setAge(12);

        StudentWithoutFaculty studentWithoutFaculty1 = new StudentWithoutFaculty(1L, "Студент 1");
        StudentWithoutFaculty studentWithoutFaculty2 = new StudentWithoutFaculty(2L, "Студент 2");

        // Настраиваем мок сервиса
        Mockito.when(service.getStudentByAgeBetween(10, 12)).thenReturn(java.util.List.of(student1, student2));
        Mockito.when(mapper.toStudentWithoutFaculty(student1)).thenReturn(studentWithoutFaculty1);
        Mockito.when(mapper.toStudentWithoutFaculty(student2)).thenReturn(studentWithoutFaculty2);

        // Выполнение и проверка
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/students/age-range")
                        .param("min", "10")
                        .param("max", "12")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Студент 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Студент 2"));
    }

    @Test
    void testGetStudentByAgeRangeNotFound() throws Exception {
        // Подготовка моков - пустой список студентов
        Mockito.when(service.getStudentByAgeBetween(20, 30)).thenReturn(java.util.List.of());

        // Выполнение и проверка - должен вернуть 404
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/students/age-range")
                        .param("min", "20")
                        .param("max", "30")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetFacultyByStudentId() throws Exception {
        // Подготовка моков для метода getFacultyByStudentId
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Гриффиндор");
        faculty.setColor("красный");

        FacultyWithoutStudents facultyWithoutStudents = new FacultyWithoutStudents(1L, "Гриффиндор", "красный");

        Mockito.when(service.getFacultyByStudentId(1L)).thenReturn(faculty);
        Mockito.when(facultyMapper.toFacultyWithoutStudents(faculty)).thenReturn(facultyWithoutStudents);

        // Выполнение и проверка
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/students/1/faculty")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Гриффиндор"))
                .andExpect(jsonPath("$.color").value("красный"));
    }

    @Test
    void testGetFacultyByStudentIdNotFound() throws Exception {
        // Подготовка моков - студент без факультета
        Mockito.when(service.getFacultyByStudentId(1L)).thenReturn(null);

        // Выполнение и проверка
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/students/1/faculty")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
