package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.ecxeption.EntityNotFoundException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;

@RestController
@RequestMapping("/student")
@Tag(name = "Student Controller", description = "Управление студентами")
public class StudentController {
    private final StudentService service;

    @Autowired
    public StudentController(StudentService service) {
        this.service = service;
    }

    @Operation(summary = "Добавить студента")
    @PostMapping
    public ResponseEntity<Student> addStudent(@RequestBody Student student) {
        Student addedStudent = service.addStudent(student);
        return ResponseEntity.ok(addedStudent);
    }

    @Operation(summary = "Получить студента по ID")
    @GetMapping("{id}")
    public ResponseEntity<Student> getStudent(
            @Parameter(description = "ID студента")
            @PathVariable Long id) {
        Student student = service.getStudent(id);
        if (student == null) {
            throw new EntityNotFoundException("Студент с id " + id + " не найден");
        }
        return ResponseEntity.ok(student);
    }

    @Operation(summary = "Обновить данные студента")
    @PutMapping
    public ResponseEntity<Student> updateStudent(@RequestBody Student student) {
        Student updatedStudent = service.updateStudent(student.getId(), student);

        return ResponseEntity.ok(updatedStudent);
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
    public ResponseEntity<Collection<Student>> getAllStudents() {
        Collection<Student> allStudents = service.getAllStudents();
        if (allStudents.isEmpty()) {
            throw new EntityNotFoundException("Студенты не найдены");
        }
        return ResponseEntity.ok(allStudents);
    }

    @Operation(summary = "Фильтр по возрасту")
    @GetMapping("/age/{age}")
    public ResponseEntity<Collection<Student>> getStudentByAge(@PathVariable int age) {
        Collection<Student> filteredStudents = service.getStudentByAge(age);
        if (filteredStudents.isEmpty()) {
             throw new EntityNotFoundException("Студенты возрастом " + age + " не найдены");
        }
        return ResponseEntity.ok(filteredStudents);
    }

}
