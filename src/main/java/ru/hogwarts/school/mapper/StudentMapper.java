package ru.hogwarts.school.mapper;

import org.springframework.stereotype.Component;
import ru.hogwarts.school.dto.FacultyWithoutStudents;
import ru.hogwarts.school.dto.StudentWithFaculty;
import ru.hogwarts.school.dto.StudentWithoutFaculty;
import ru.hogwarts.school.model.Student;

@Component
public class StudentMapper {
    public StudentWithFaculty toStudentWithFaculty(Student student){
        return new StudentWithFaculty(
                student.getId(),
                student.getName(),
                student.getAge(),
                student.getFaculty()!=null
                ?new FacultyWithoutStudents(
                        student.getFaculty().getId(),
                        student.getFaculty().getName())
                        :null
                );
        }

    public StudentWithoutFaculty toStudentWithoutFaculty(Student student) {
        return new StudentWithoutFaculty(
                student.getId(),
                student.getName()
        );
    }
}
