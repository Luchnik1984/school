package ru.hogwarts.school.dto;

import java.util.List;

public record FacultyWithStudents(
        long id,
        String name,
        String color,
        List<StudentWithoutFaculty> students
) {
}
