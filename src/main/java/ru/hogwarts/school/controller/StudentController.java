package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;
import java.util.Collection;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService service;

    @Autowired
    public StudentController(StudentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Student> addStudent(@RequestBody Student student){
        Student adddedStudent = service.addStudent(student);
        return ResponseEntity.ok(adddedStudent);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable Long id){
        Student student= service.getStudent(id);
        if (student==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @PutMapping
    public ResponseEntity<Student> updateStudent(@RequestBody Student student){
        Student updatedStudent=service.updateStudent(student.getId(),student);
        if (updatedStudent==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id){
        service.removeStudent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Collection<Student>> getAllStudents(){
        return ResponseEntity.ok(service.getAllStudents().values());
    }

    @GetMapping("/age/{age}")
    public ResponseEntity<Collection<Student>> getStudentByAge(@PathVariable int age){
        Collection<Student> filteredStudents = service.getStudentByAge(age);
        if (filteredStudents.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(filteredStudents);
    }

}
