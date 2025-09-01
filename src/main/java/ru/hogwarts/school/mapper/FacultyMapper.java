package ru.hogwarts.school.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.hogwarts.school.dto.FacultyWithStudents;
import ru.hogwarts.school.dto.FacultyWithoutStudents;
import ru.hogwarts.school.dto.StudentWithoutFaculty;
import ru.hogwarts.school.model.Faculty;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class FacultyMapper {
    private final StudentMapper studentMapper;

    @Autowired
    public FacultyMapper(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    public FacultyWithStudents toFacultyWithStudents(Faculty faculty) {

        return new FacultyWithStudents(
                faculty.getId(),
                faculty.getName(),
                faculty.getColor(),
                faculty.getStudents() != null ?
                        faculty.getStudents().stream()
                                .map(studentMapper::toStudentWithoutFaculty)
                                .collect(Collectors.toList())
                        : Collections.emptyList()
        );
    }

    public FacultyWithoutStudents toFacultyWithoutStudents(Faculty faculty) {
        if (faculty==null) {
            return null;
        }
        return new FacultyWithoutStudents(
                faculty.getId(),
                faculty.getName(),
                faculty.getColor()
        );
    }
}