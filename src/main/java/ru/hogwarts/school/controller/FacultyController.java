package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(addedFaculty);
    }

    @Operation(summary = "Обновить данные факультета")
    @PutMapping
    ResponseEntity<Faculty> updateFaculty(@RequestBody Faculty faculty){
     Faculty updetedFaculty=service.updateFaculty(faculty);
     if (updetedFaculty==null){
         return ResponseEntity.badRequest().build();
     }
     return ResponseEntity.ok(updetedFaculty);
    }

    @Operation(summary = "Удалить факультет")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteFaculty(
            @Parameter(description = "ID факультета")
            @PathVariable Long id){
        service.removeFaculty(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить все факультеты")
    @GetMapping
    public ResponseEntity<Collection<Faculty>> getAllFaculties(){
        return ResponseEntity.ok(service.getAllFaculties().values());
    }

    @Operation(summary = "Фильтр по цвету")
    @GetMapping("/color/{color}")
    public ResponseEntity<Collection<Faculty>> getFacultyByColor(
            @Parameter(description = "Цвет факультета")
            @PathVariable String color){
        Collection<Faculty> filteredFaculties = service.getFacultyByColor(color);
        if (filteredFaculties.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(filteredFaculties);
    }

}

