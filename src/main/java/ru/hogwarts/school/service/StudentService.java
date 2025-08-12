package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final Map<Long, Student> students = new HashMap<>();
    private long lastId = 0;

    public Student addStudent(Student student) {
        student.setId(++lastId);
        students.put(student.getId(), student);
        return student;
    }

    public Student getStudent(long id) {
        return students.get(id);
    }

    public Student updateStudent(long id, Student student) {
        if (student == null || !students.containsKey(id)) {
            return null;
        }
        students.put(id, student);
        return student;
    }


    public Student removeStudent(long id) {
        return students.remove(id);
    }

    public Map<Long, Student> getAllStudents() {
        return new HashMap<>(students);
    }

    public Collection<Student> getStudentByAge(int age) {
        return students.values().stream()
                .filter(student -> student.getAge() == age)
                .collect(Collectors.toList());
    }

}
