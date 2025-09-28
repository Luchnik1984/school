package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;
import java.util.stream.Collectors;


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

        Collection<Student> students = studentRepository.findAll();
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
        Faculty faculty = facultyRepository.findByStudentsId(studentId);
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

    public Double getAverageAge() {
        logger.info("Was invoked method for get average age of students");
        Double averageAge = studentRepository.findAverageAge();
        logger.debug(" Average age of students {}", averageAge);
        return averageAge;
    }

    public List<Student> getLastFiveStudents() {
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

        List<Student> allStudents = studentRepository.findAll();
        logger.debug("Found {} students in database", allStudents.size());

        List<String> result = allStudents.stream()
                .map(Student::getName)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .map(String::toUpperCase)
                .filter(name -> name.startsWith(searchLetter))
                .sorted()
                .collect(Collectors.toList());

        logger.debug("Found {} students with names starting with letter {}", result.size(), searchLetter);

        if (result.isEmpty()) {
            logger.warn("No students found with names starting with letter {}", searchLetter);
        } else {
            logger.info("Student names starting with {}: {}", searchLetter, result);
        }
        return result;
    }

    public Double getAverageAgeOfAllStudents() {
        logger.info("Was invoked method for get average age of all students");

        List<Student> allStudents = studentRepository.findAll();
        logger.debug("Found {} students", allStudents.size());

        if (allStudents.isEmpty()) {
            logger.warn("No students found - can't calculate average age of all students");
            return 0.0;
        }
        DoubleSummaryStatistics stats = allStudents.stream()
                .mapToDouble(Student::getAge)
                .summaryStatistics();

        double averageAge = stats.getAverage();
        logger.info("Age statistics - count: {}, average: {}, Min: {}, Max: {}",
                stats.getCount(), averageAge, stats.getMin(), stats.getMax());

        return averageAge;
    }

    /**
     * Получить имена студентов для параллельного вывода
     */
    public List<String> getStudentNamesForParallelPrinting() {
        logger.info("Was invoked method for get student names for parallel printing");

        List<Student> allStudents = studentRepository.findAll();
        logger.debug("Found {} total students in database", allStudents.size());

        List<String> studentNames = allStudents.stream()
                .map(Student::getName)
                .filter(Objects::nonNull)
                .filter(name -> !name.trim().isEmpty())
                .collect(Collectors.toList());

        logger.debug("Student names for parallel printing: {}", studentNames);
        return studentNames;
    }


    /**
     * Универсальный метод для вывода имен студентов с опцией синхронизации
     */
    private final Object printLock = new Object();

    public Map<String, Object> printStudentNamesUniversal(List<String> studentNames, boolean synchronizedPrint) {
        logger.info("Printing {} student names in {} mode",
                studentNames.size(), synchronizedPrint ? "synchronized" : "parallel");

        int totalStudents = studentNames.size();
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, String>> printResults = new ArrayList<>();

        result.put("total_students", totalStudents);
        result.put("printed_students", Math.min(totalStudents, 6));
        result.put("synchronized", synchronizedPrint);

        // Вывод в основном потоке
        if (synchronizedPrint) {
            synchronized (printLock) {
                printStudentsInRange(studentNames, 0, 2, "main", printResults);
            }
        } else {
            printStudentsInRange(studentNames, 0, 2, "main", printResults);
        }

        // Создаем и запускаем параллельные потоки
        List<Thread> threads = createUniversalThreads(studentNames, totalStudents, printResults, synchronizedPrint);
        startAndWaitThreads(threads);

        result.put("print_results", printResults);
        result.put("threads_count", threads.size() + 1);

        logger.info("Printing completed for {} students in {} mode",
                totalStudents, synchronizedPrint ? "synchronized" : "parallel");
        return result;
    }

    /**
     * Создание универсальных потоков для обоих режимов
     */
    private List<Thread> createUniversalThreads(List<String> studentNames, int totalStudents,
                                                List<Map<String, String>> printResults, boolean synchronizedPrint) {
        List<Thread> threads = new ArrayList<>();

        // Поток 1: для студентов 3 и 4
        if (totalStudents >= 3) {
            Thread thread1 = new Thread(() -> {
                if (synchronizedPrint) {
                    synchronized (printLock) {
                        printStudentsInRange(studentNames, 2, 4, "parallel-1", printResults);
                    }
                } else {
                    printStudentsInRange(studentNames, 2, 4, "parallel-1", printResults);
                }
            });
            thread1.setName("ParallelThread-1");
            threads.add(thread1);
        }

        // Поток 2: для студентов 5 и 6
        if (totalStudents >= 5) {
            Thread thread2 = new Thread(() -> {
                if (synchronizedPrint) {
                    synchronized (printLock) {
                        printStudentsInRange(studentNames, 4, 6, "parallel-2", printResults);
                    }
                } else {
                    printStudentsInRange(studentNames, 4, 6, "parallel-2", printResults);
                }
            });
            thread2.setName("ParallelThread-2");
            threads.add(thread2);
        }

        return threads;
    }

    /**
     * Вывод студентов в диапазоне (без внутренней синхронизации)
     */
    private void printStudentsInRange(List<String> studentNames, int start, int end,
                                      String threadType, List<Map<String, String>> printResults) {
        for (int i = start; i < end && i < studentNames.size(); i++) {
            printStudent(threadType, i + 1, studentNames.get(i), printResults);
        }
    }

    /**
     * Базовый вывод
     */
    private void printStudent(String threadType, int studentNumber, String studentName,
                              List<Map<String, String>> printResults) {
        String message = formatMessage(threadType, studentNumber, studentName);
        System.out.println(message);
        addPrintResult(printResults, threadType, studentNumber, studentName, message);
    }


    /**
     * Форматирование сообщения (общая логика)
     */
    private String formatMessage(String threadType, int studentNumber, String studentName) {
        String threadName = threadType.equals("main") ? "Main thread" :
                "Parallel thread " + threadType.split("-")[1];
        return threadName + " - Student " + studentNumber + ": " + studentName;
    }

    private void addPrintResult(List<Map<String, String>> printResults, String threadType,
                                int studentNumber, String studentName, String message) {
        Map<String, String> result = new HashMap<>();
        result.put("thread_type", threadType);
        result.put("thread_name", threadType.equals("main") ? "Main Thread" : "Parallel Thread " + threadType.split("-")[1]);
        result.put("student_number", String.valueOf(studentNumber));
        result.put("student_name", studentName);
        result.put("message", message);

        printResults.add(result);
    }

    private void startAndWaitThreads(List<Thread> threads) {
        // Запускаем поток
        threads.forEach(Thread::start);
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                // прерываем текущий поток
                Thread.currentThread().interrupt();
            }
        });
    }

    /**
     * Создает простой ответ (для случая без студентов)
     */
    public Map<String, Object> createSimpleResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "No students found");
        response.put("total_students", 0);
        response.put("printed_students", 0);
        response.put("status", "success");
        response.put("print_results", Collections.emptyList());
        return response;
    }
}
