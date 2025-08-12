package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FacultyService {
    private final Map<Long, Faculty> faculties = new HashMap<>();
    private long lastId = 0;

    public Faculty addFaculty(Faculty faculty) {
        faculty.setId(++lastId);
        faculties.put(faculty.getId(), faculty);
        return faculty;
    }

    public Faculty getFaculty(long id) {
        return faculties.get(id);
    }

    public Faculty updateFaculty(long id, Faculty faculty) {
        if (faculty == null || !faculties.containsKey(faculty.getId())) {
            return null;
        }
        faculties.put(id, faculty);
        return faculty;
    }


    public Faculty removeFaculty(long id) {
        return faculties.remove(id);
    }

    public Map<Long, Faculty> getAllFaculties() {
        return new HashMap<>(faculties);
    }

    public Collection<Faculty> getFacultyByColor(String color) {
        return faculties.values().stream()
                .filter(faculty -> faculty.getColor().equalsIgnoreCase(color))
                .collect(Collectors.toList());
    }
}
