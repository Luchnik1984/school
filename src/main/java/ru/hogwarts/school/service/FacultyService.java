package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.ecxeption.EntityAlreadyExistsException;
import ru.hogwarts.school.ecxeption.EntityNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;

@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty addFaculty(Faculty faculty) {
        if (facultyRepository.existsByName(faculty.getName())){
            throw new EntityAlreadyExistsException(
                    "Факультет с названием " + faculty.getName() + " уже существует");
        }
        return facultyRepository.save(faculty);
    }

    public Faculty getFaculty(long id) {

        return facultyRepository.findById(id).orElse(null);
    }

    public Faculty updateFaculty(long id, Faculty faculty) {
        return facultyRepository.findById(id)
                .map(existingFaculty -> {
                    if (!existingFaculty.getName().equalsIgnoreCase(faculty.getName())
                        && facultyRepository.existsByName(faculty.getName())){
                        throw new EntityAlreadyExistsException(
                                "Факультет с названием " + faculty.getName() + " уже существует");
                    }
                    faculty.setId(id);
                    return facultyRepository.save(faculty);
                })
                .orElseThrow(() -> new EntityNotFoundException("Факультет с id " + id + " не найден"));
    }


    public void removeFaculty(long id) {
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> getAllFaculties() {
        return facultyRepository.findAll();

    }

    public Collection<Faculty> getFacultyByColor(String color) {
        return facultyRepository.findByColorIgnoreCase(color);
    }
}
