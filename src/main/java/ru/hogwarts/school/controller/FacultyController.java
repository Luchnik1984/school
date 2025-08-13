package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.exeption.EntityNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;

@RestController
@RequestMapping("/faculty")
@Tag(name = "Faculty Controller", description = "Управление факультетами")
public class FacultyController {
    private final FacultyService service;

    @Autowired
    public FacultyController(FacultyService service) {
        this.service = service;
    }

    @Operation(summary = "Добавить факультет")
    @PostMapping
    ResponseEntity<Faculty> addFaculty(@RequestBody Faculty faculty) {
        Faculty addedFaculty = service.addFaculty(faculty);
        return ResponseEntity.ok(addedFaculty);
    }

    @Operation(summary = "Получить факультет по ID")
    @GetMapping("{id}")
    ResponseEntity<Faculty> getFaculty(
            @Parameter(description = "ID факультета")
            @PathVariable Long id) {
        Faculty addedFaculty = service.getFaculty(id);
        if (addedFaculty == null) {
            throw new EntityNotFoundException("Факультет с id " + id + " не найден");
        }
        return ResponseEntity.ok(addedFaculty);
    }

    @Operation(summary = "Обновить данные факультета")
    @PutMapping
    ResponseEntity<Faculty> updateFaculty(@RequestBody Faculty faculty) {
        Faculty updatedFaculty = service.updateFaculty(faculty.getId(), faculty);

        return ResponseEntity.ok(updatedFaculty);
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
    public ResponseEntity<Collection<Faculty>> getAllFaculties() {
        Collection<Faculty> allFaculties=service.getAllFaculties();
        if (allFaculties.isEmpty()) {
            throw new EntityNotFoundException("Факультеты не найдены");
        }
        return ResponseEntity.ok(service.getAllFaculties());
    }

    @Operation(summary = "Фильтр по цвету")
    @GetMapping("/color/{color}")
    public ResponseEntity<Collection<Faculty>> getFacultyByColor(
            @Parameter(description = "Цвет факультета")
            @PathVariable String color) {
        Collection<Faculty> filteredFaculties = service.getFacultyByColor(color);
        if (filteredFaculties.isEmpty()) {
            throw new EntityNotFoundException("Факультет " + color + " не найден");
        }
        return ResponseEntity.ok(filteredFaculties);
    }

}

