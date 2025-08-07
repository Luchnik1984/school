package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;
import java.util.Collection;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService service;

    @Autowired
    public FacultyController(FacultyService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<Faculty> addFaculty(@RequestBody Faculty faculty) {
        Faculty addedFaculty = service.addFaculty(faculty);
        return ResponseEntity.ok(addedFaculty);
    }

    @GetMapping("/{id}")
    ResponseEntity<Faculty> getFaculty(@PathVariable Long id) {
        Faculty addedFaculty = service.getFaculty(id);
        if (addedFaculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(addedFaculty);
    }

    @PutMapping
    ResponseEntity<Faculty> updateFaculty(@RequestBody Faculty faculty){
     Faculty updetedFaculty=service.updateFaculty(faculty.getId(),faculty);
     if (updetedFaculty==null){
         return ResponseEntity.notFound().build();
     }
     return ResponseEntity.ok(updetedFaculty);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Long id){
        service.removeFaculty(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Collection<Faculty>> getAllFaculties(){
        return ResponseEntity.ok(service.getAllFaculties().values());
    }

    @GetMapping("/color/{color}")
    public ResponseEntity<Collection<Faculty>> getFacultyByColor(@PathVariable String color){
        Collection<Faculty> filteredFaculties = service.getFacultyByColor(color);
        if (filteredFaculties.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(filteredFaculties);
    }

}

