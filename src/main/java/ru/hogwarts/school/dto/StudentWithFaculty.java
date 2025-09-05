package ru.hogwarts.school.dto;

public record StudentWithFaculty(
        long id,
        String name,
        int age,
        FacultyWithoutStudents faculty
){
}
