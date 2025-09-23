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
import java.util.stream.Collectors;
import java.util.Objects;


@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final Logger logger = LoggerFactory.getLogger(StudentService.class);

    public StudentService(StudentRepository studentRepository, FacultyRepository facultyRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }

    public Student addStudent(Student student) {
        logger.info("Was invoked method for create student");
        return studentRepository.save(student);
    }

    public Student getStudent(long id) {
        logger.info("Was invoked method for get student with id = {}", id);

       Student student = studentRepository.findById(id).orElse(null);
       if (student == null) {
           logger.warn("Student with id {} not found", id);
       }
       return student;
    }

    public Student updateStudent(long id, Student student) {
        logger.info("Was invoked method for update student with id = {}", id);
        return studentRepository.findById(id)
                .map(existingStudent -> {
                    logger.debug("Updating student: name from {} to {}, age from {} to {}",
                            existingStudent.getName(), student.getName(),
                            existingStudent.getAge(), student.getAge());
                    existingStudent.setName(student.getName());
                    existingStudent.setAge(student.getAge());
                    existingStudent.setFaculty(student.getFaculty());
                    return studentRepository.save(existingStudent);
                })
                .orElse(null);
    }


    public void removeStudent(long id) {
        logger.info("Was invoked method for remove student with id = {}", id);
        if (!studentRepository.existsById(id)) {
            logger.warn("Attempt to remove student with id {} which does not exist", id);
        }
        studentRepository.deleteById(id);
    }

    public Collection<Student> getAllStudents() {
        logger.info("Was invoked method for get all students");

        Collection<Student>students = studentRepository.findAll();
        logger.debug("Found {} students", students.size());
        return students;
    }


    public Collection<Student> getStudentByAge(int age) {
        logger.info("Was invoked method for get student by age {}", age);

        Collection<Student> students = studentRepository.findByAge(age);
        logger.debug("Found {} students with age {}", students.size(), age);
        return students;
    }

    public Collection<Student> getStudentByAgeBetween(int minAge, int maxAge) {
        logger.info("Was invoked method for get student by age between {} and {}", minAge, maxAge);
        Collection<Student> students = studentRepository.findByAgeBetween(minAge, maxAge);
        logger.debug("Found {} students with age between {} and {}", students.size(), minAge, maxAge);
        return students;
    }

    public Faculty getFacultyByStudentId(Long studentId) {
        logger.info("Was invoked method for get faculty by student id {}", studentId);
        Faculty faculty =  facultyRepository.findByStudentsId(studentId);
        if (faculty == null) {
            logger.debug("No faculty found for student id {}", studentId);
        }
        return faculty;
    }

    public Integer getTotalCount() {
        logger.info("Was invoked method for get total students count");
        Integer count = studentRepository.countAllStudents();
        logger.debug(" Total students count {}", count);
        return count;
    }

    public Double getAverageAge(){
        logger.info("Was invoked method for get average age of students");
        Double avarageAge = studentRepository.findAverageAge();
        logger.debug(" Average age of students {}", avarageAge);
        return avarageAge;
    }

    public List<Student> getLastFiveStudents(){
        logger.info("Was invoked method for get last five students");
        List<Student> students = studentRepository.findLastFiveStudents();
        logger.debug("Found {} last students", students.size());
        return students;
    }

    public List<String> getStudentNamesStartingWithLetter(String letter) {
        logger.info("Was invoked method for get student names starting with letter{}", letter);

        if (letter == null || letter.trim().isEmpty()) {
            logger.warn("Invalid letter parameter: '{}'. Using default latin letter 'A'", letter);
            letter = "A"; // Значение по умолчанию (латиница)
        }

        // Если передадут строку, ищем по первой букве
        String searchLetter = letter.trim().toUpperCase().substring(0, 1);
        logger.debug("Searching for names starting with letter {}", searchLetter);

        List<Student>allStudents = studentRepository.findAll();
        logger.debug("Found {} students in database", allStudents.size());

        List<String> result = allStudents.stream()
                .map(Student::getName)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(name->!name.isEmpty())
                .map(String::toUpperCase)
                .filter(name ->name.startsWith(searchLetter))
                .sorted()
                .collect(Collectors.toList());

        logger.debug("Found {} students with names starting with letter {}", result.size(), searchLetter);

        if (result.isEmpty()){
            logger.warn("No students found with names starting with letter {}", searchLetter);
        } else {
            logger.info("Student names starting with {}: {}", searchLetter, result);
        }
        return result;
    }

}
