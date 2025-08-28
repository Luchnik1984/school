package ru.hogwarts.school.mapper;

import org.springframework.stereotype.Component;
import ru.hogwarts.school.dto.FacultyWithStudents;
import ru.hogwarts.school.dto.FacultyWithoutStudents;
import ru.hogwarts.school.dto.StudentWithoutFaculty;
import ru.hogwarts.school.model.Faculty;

import java.util.stream.Collectors;

@Component
public class FacultyMapper {
    public FacultyWithStudents toFacultyWithStudents(Faculty faculty) {

        return new FacultyWithStudents(
                faculty.getId(),
                faculty.getName(),
                faculty.getColor(),
                faculty.getStudents().stream()
                        .map(student -> new StudentWithoutFaculty(
                                student.getId(),
                                student.getName()))
                        .collect(Collectors.toList())
        );
    }

    public FacultyWithoutStudents toFacultyWithoutStudents(Faculty faculty) {
        return new FacultyWithoutStudents(
                faculty.getId(),
                faculty.getName(),
                faculty.getColor()
        );
    }
}