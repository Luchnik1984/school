package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.List;

@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;
    private final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    public FacultyService(FacultyRepository facultyRepository, StudentRepository studentRepository) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
    }

    public Faculty addFaculty(Faculty faculty) {
        logger.info("Was invoked method for create faculty");
        return facultyRepository.save(faculty);
    }

    public Faculty getFaculty(long id) {
        logger.info("Was invoked method for get faculty with id {}", id);
        Faculty faculty = facultyRepository.findById(id).orElse(null);
        if (faculty == null) {
            logger.warn("Faculty with id = {} not found", id);
        }
        return faculty;
    }

    public Faculty updateFaculty(long id, Faculty faculty) {
        logger.info("Was invoked method for update faculty with id {}", id);
        return facultyRepository.findById(id)
                .map(existingFaculty -> {
                    logger.debug("Updating faculty name = from '{}' to '{}', color from '{}' to '{}'",
                            existingFaculty.getName(), faculty.getName(),
                            existingFaculty.getColor(), faculty.getColor());
                    existingFaculty.setName(faculty.getName());
                    existingFaculty.setColor(faculty.getColor());
                    return facultyRepository.save(existingFaculty);
                })
                .orElse(null);
    }


    public void removeFaculty(long id) {
        logger.info("Was invoked method for delete faculty with id = {}", id);
        if (!facultyRepository.existsById(id)) {
            logger.warn("Attempt to remove non-existent faculty with id = {}", id);
        }
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> getAllFaculties() {
        logger.info("Was invoked method for get all faculties");
        Collection<Faculty> faculties = facultyRepository.findAll();
        logger.debug("Found {} faculties", faculties.size());
        return faculties;
    }

    public Collection<Faculty> getFacultyByColor(String color) {
        logger.info("Was invoked method for get faculty by color {}", color);
        Collection<Faculty> faculties = facultyRepository.findByColorIgnoreCase(color);
        logger.debug("Found {} faculties with color {}", faculties.size(), color);
        return faculties;
    }

    public Collection<Faculty> getFacultiesByNameOrColor(String query) {
        logger.info("Was invoked method for get faculty by name or color: {}", query);
        Collection<Faculty> faculties = facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(query,query);
        logger.debug("Found {} faculties matching query {}", faculties.size(), query);
        return faculties;
    }

    public List<Student> getStudentsByFacultyId(Long facultyId) {
        logger.info("Was invoked method for get students by faculty id {}", facultyId);
        List<Student> students = studentRepository.findByFacultyId(facultyId);
        logger.debug("Found {} students for faculty id {}", students.size(), facultyId);
        return students;
    }
}
