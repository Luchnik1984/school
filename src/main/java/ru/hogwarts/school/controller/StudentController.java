package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.FacultyWithoutStudents;
import ru.hogwarts.school.dto.StudentWithFaculty;
import ru.hogwarts.school.dto.StudentWithoutFaculty;
import ru.hogwarts.school.mapper.FacultyMapper;
import ru.hogwarts.school.mapper.StudentMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/students")
@Tag(name = "Student Controller", description = "Управление студентами")
public class StudentController {
    private final StudentService service;
    private final StudentMapper mapper;
    private final FacultyMapper facultyMapper;
    private final Logger logger = LoggerFactory.getLogger(StudentController.class);


    public StudentController(StudentService service, StudentMapper mapper, FacultyMapper facultyMapper) {
        this.service = service;
        this.mapper = mapper;
        this.facultyMapper = facultyMapper;
    }

    @Operation(summary = "Добавить студента")
    @PostMapping
    public ResponseEntity<StudentWithFaculty> addStudent(@RequestBody Student student) {
        Student addedStudent = service.addStudent(student);
        return ResponseEntity.ok(mapper.toStudentWithFaculty(addedStudent));
    }

    @Operation(summary = "Получить студента по ID")
    @GetMapping("{id}")
    public ResponseEntity<StudentWithFaculty> getStudent(
            @Parameter(description = "ID студента")
            @PathVariable Long id) {
        Student student = service.getStudent(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapper.toStudentWithFaculty(student));
    }

    @Operation(summary = "Обновить данные студента")
    @PutMapping
    public ResponseEntity<StudentWithFaculty> updateStudent(@RequestBody Student student) {
        Student updatedStudent = service.updateStudent(student.getId(), student);
        if (updatedStudent == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(mapper.toStudentWithFaculty(updatedStudent));
    }

    @Operation(summary = "Удалить студента")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteStudent(
            @Parameter(description = "ID студента")
            @PathVariable Long id) {
        service.removeStudent(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить всех студентов")
    @GetMapping
    public ResponseEntity<Collection<StudentWithoutFaculty>> getAllStudents() {
        return ResponseEntity.ok(service.getAllStudents().stream()
                .map(mapper::toStudentWithoutFaculty)
                .collect(Collectors.toList()));
    }

    @Operation(summary = "Фильтр по возрасту")
    @GetMapping("/age/{age}")
    public ResponseEntity<Collection<StudentWithoutFaculty>> getStudentByAge(@PathVariable int age) {
        Collection<Student> filteredStudents = service.getStudentByAge(age);
        if (filteredStudents.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(filteredStudents.stream()
                .map(mapper::toStudentWithoutFaculty)
                .collect(Collectors.toList()));

    }

    @Operation(summary = "Фильтр по возрасту в диапазоне")
    @GetMapping("/age-range")
    public ResponseEntity<Collection<StudentWithoutFaculty>> getStudentByAgeBetween(
            @Parameter(description = "минимальный возраст") @RequestParam int min,
            @Parameter(description = "максимальный возраст") @RequestParam int max) {
        Collection<Student> filteredStudents = service.getStudentByAgeBetween(min, max);
        if (filteredStudents.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(filteredStudents.stream()
                .map(mapper::toStudentWithoutFaculty)
                .collect(Collectors.toList()));
    }

    @Operation(summary = "получить факультет студента")
    @GetMapping("{id}/faculty")
    public ResponseEntity<FacultyWithoutStudents> getFacultyByStudentId(
            @Parameter(description = "Id студента") @PathVariable Long id
    ) {
        Faculty faculty = service.getFacultyByStudentId(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(facultyMapper.toFacultyWithoutStudents(faculty));
    }

    @Operation(summary = "Получить общее количество студентов")
    @GetMapping("/count")
    public ResponseEntity<Integer> getTotalStudentsCount() {
        Integer count = service.getTotalCount();
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Получить средний возраст студентов")
    @GetMapping("/average-age")
    public ResponseEntity<Double> getAverageAge() {
        Double averageAge = service.getAverageAge();
        return ResponseEntity.ok(averageAge);
    }

    @Operation(summary = "Получить 5 последних студентов")
    @GetMapping("/last-five")
    public ResponseEntity<List<StudentWithFaculty>> getLastFiveStudents() {
        List<Student> students = service.getLastFiveStudents();
        List<StudentWithFaculty> result = students.stream()
                .map(mapper::toStudentWithFaculty)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Получить имена студентов, начинающиеся на указанную букву")
    @GetMapping("/names-starting-with")
    public ResponseEntity<List<String>> getNamesStartingWithLetter(
            @Parameter(description = "Буква для поиска (по умолчанию - A латинская)")
            @RequestParam(defaultValue = "A") String letter) {

        logger.info("Was invoked endpoint for get student names starting with letter: {}", letter);
        List<String> studentNames = service.getStudentNamesStartingWithLetter(letter);

        if (studentNames.isEmpty()) {
            logger.warn("No students found with names starting with letter: {}", letter);
            return ResponseEntity.notFound().build();
        }

        logger.debug("Returning {} student names starting with {}", studentNames.size(), letter);
        return ResponseEntity.ok(studentNames);
    }

    @Operation(summary = "Получить средний возраст всех студентов")
    @GetMapping("/average-age-all")
    public ResponseEntity<Double> getAverageAgeOfAllStudents() {
        logger.info("Was invoked endpoint for get average age of all students");
        Double averageAge = service.getAverageAgeOfAllStudents();

        if (averageAge == 0.0) {
            logger.warn("Average age is 0 - possibly no students in the database");
        }
        logger.debug("Returning average age: {}", averageAge);
        return ResponseEntity.ok(averageAge);
    }


    @Operation(summary = "Вывести имена студентов в параллельных потоках")
    @GetMapping("/print-parallel")
    public ResponseEntity<Map<String, Object>> printStudentNamesInParallel() {
        logger.info("Was invoked endpoint for print student names in parallel");

        List<String> studentNames = service.getStudentNamesForParallelPrinting();

        if (studentNames.isEmpty()) {
            return ResponseEntity.ok(createSimpleResponse());
        }

        Map<String, Object> result = service.printStudentNamesUniversal(studentNames, false);
        result.put("message", "Student names printed in parallel successfully");
        result.put("status", "success");

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Вывести имена студентов в синхронизированных параллельных потоках")
    @GetMapping("/print-synchronized")
    public ResponseEntity<Map<String, Object>> printStudentNamesSynchronized() {
        logger.info("Was invoked endpoint for print student names with synchronization");

        List<String> studentNames = service.getStudentNamesForParallelPrinting();

        if (studentNames.isEmpty()) {
            return ResponseEntity.ok(createSimpleResponse());
        }

        Map<String, Object> result = service.printStudentNamesUniversal(studentNames, true);
        result.put("message", "Student names printed with synchronization successfully");
        result.put("status", "success");

        return ResponseEntity.ok(result);
    }

    /**
     * Создает простой ответ (для случая без студентов)
     */
    private Map<String, Object> createSimpleResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "No students found");
        response.put("total_students", 0);
        response.put("printed_students", 0);
        response.put("status", "success");
        response.put("print_results", Collections.emptyList());
        return response;
    }

}

