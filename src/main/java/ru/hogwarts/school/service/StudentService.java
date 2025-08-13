package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exeption.EntityAlreadyExistsException;
import ru.hogwarts.school.exeption.EntityNotFoundException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;


@Service
public class StudentService {
    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student addStudent(Student student) {
        if (studentRepository.existsByName(student.getName())) {
            throw new EntityAlreadyExistsException("Студент с именем " + student.getName() + " уже существует");
        }
            return studentRepository.save(student);
        }


    public Student getStudent(long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public Student updateStudent(long id, Student student) {
        return studentRepository.findById(id)
                .map(existingStudent -> {
                    if (!existingStudent.getName().equalsIgnoreCase(student.getName())
                        && studentRepository.existsByName(student.getName())){
                        throw new EntityAlreadyExistsException(
                                "Студент с именем " + student.getName() + " уже существует");
                    }
                    student.setId(id);
                    return studentRepository.save(student);
                })
                .orElseThrow(() ->new EntityNotFoundException(
                        "Студент с id " + id + " не найден"));
    }


    public void removeStudent(long id) {
        studentRepository.deleteById(id);
    }

    public Collection<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Collection<Student> getStudentByAge(int age) {
        return studentRepository.findByAge(age);


    }

}
