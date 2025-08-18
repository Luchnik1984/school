package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.FacultyWithStudents;
import ru.hogwarts.school.dto.FacultyWithoutStudents;
import ru.hogwarts.school.dto.StudentWithoutFaculty;
import ru.hogwarts.school.mapper.FacultyMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/faculty")
@Tag(name = "Faculty Controller", description = "Управление факультетами")
public class FacultyController {
    private final FacultyService service;
    private final FacultyMapper mapper;


    public FacultyController(FacultyService service, FacultyMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Добавить факультет")
    @PostMapping
    ResponseEntity<FacultyWithStudents> addFaculty(@RequestBody Faculty faculty) {
        Faculty addedFaculty = service.addFaculty(faculty);
        return ResponseEntity.ok(mapper.toFacultyWithStudents(addedFaculty));
    }

    @Operation(summary = "Получить факультет по ID")
    @GetMapping("{id}")
    ResponseEntity<FacultyWithStudents> getFaculty(
            @Parameter(description = "ID факультета")
            @PathVariable Long id) {
        Faculty addedFaculty = service.getFaculty(id);
        if (addedFaculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapper.toFacultyWithStudents(addedFaculty));
    }

    @Operation(summary = "Обновить данные факультета")
    @PutMapping
    ResponseEntity<FacultyWithStudents> updateFaculty(@RequestBody Faculty faculty) {
        Faculty updatedFaculty = service.updateFaculty(faculty.getId(), faculty);
        if (updatedFaculty == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(mapper.toFacultyWithStudents(updatedFaculty));
    }

    @Operation(summary = "Удалить факультет")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteFaculty(
            @Parameter(description = "ID факультета")
            @PathVariable Long id) {
        service.removeFaculty(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить все факультеты")
    @GetMapping
    public ResponseEntity<Collection<FacultyWithoutStudents>> getAllFaculties() {
        return ResponseEntity.ok(service.getAllFaculties().stream()
                .map(mapper::toFacultyWithoutStudents)
                .collect(Collectors.toList()));
    }

    @Operation(summary = "Фильтр по цвету")
    @GetMapping("/color/{color}")
    public ResponseEntity<Collection<FacultyWithoutStudents>> getFacultyByColor(
            @Parameter(description = "Цвет факультета")
            @PathVariable String color) {
        Collection<Faculty> filteredFaculties = service.getFacultyByColor(color);
        if (filteredFaculties.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(filteredFaculties.stream()
                .map(mapper::toFacultyWithoutStudents)
                .collect(Collectors.toList()));
    }

    @Operation(summary = "Поиск факультета по имени или цвету")
    @GetMapping("/search")
    public ResponseEntity<Collection<FacultyWithoutStudents>> getFacultiesByNameOrColor(
            @Parameter(description = "название или цвет факультета") @RequestParam String query) {
        Collection<Faculty> faculties = service.getFacultiesByNameOrColor(query);
        if (faculties.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculties.stream()
                .map(mapper::toFacultyWithoutStudents)
                .collect(Collectors.toList()));
    }

    @Operation(summary = "Получить студентов факультета")
    @GetMapping("/{id}/students")
    public ResponseEntity<List<StudentWithoutFaculty>> getStudentsByFacultyId(
            @Parameter(description = "ID факультета") @PathVariable Long id) {
        List<Student> students = service.getStudentsByFacultyId(id);
        if (students.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(students.stream()
                .map(student -> new StudentWithoutFaculty(
                        student.getId(),
                        student.getName()))
                .collect(Collectors.toList()));
    }

}

