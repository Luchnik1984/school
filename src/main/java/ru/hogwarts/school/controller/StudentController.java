package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jdk.jfr.Description;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.FacultyWithoutStudents;
import ru.hogwarts.school.dto.StudentWithFaculty;
import ru.hogwarts.school.dto.StudentWithoutFaculty;
import ru.hogwarts.school.mapper.StudentMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/students")
@Tag(name = "Student Controller", description = "Управление студентами")
public class StudentController {
    private final StudentService service;
    private final StudentMapper mapper;


    public StudentController(StudentService service, StudentMapper mapper) {
        this.service = service;
        this.mapper = mapper;
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
        return ResponseEntity.ok(new FacultyWithoutStudents(
                faculty.getId(),
                faculty.getName()));
    }

}
