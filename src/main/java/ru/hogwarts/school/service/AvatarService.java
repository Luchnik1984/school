package ru.hogwarts.school.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class AvatarService {
    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    @Value("${avatars.directory.path:./avatars}")
    private String avatarsDirectory;

    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;

    }

    @PostConstruct
    public void init() {
        createAvatarsDirectory();
    }

    public Avatar createAvatar(Avatar avatar) {
        return avatarRepository.save(avatar);
    }


    public Avatar getAvatarById(Long id) {
        return avatarRepository.findById(id).orElse(null);
    }

    public Avatar getAvatarByStudentId(Long studentId) {
        return avatarRepository.findByStudentId(studentId).orElse(null);
    }

    public Avatar updateAvatar(Long id, Avatar avatar) {
        return avatarRepository.findById(id)
                .map(existingAvatar -> {
                    existingAvatar.setFilePath(avatar.getFilePath());
                    existingAvatar.setFileSize(avatar.getFileSize());
                    existingAvatar.setMediaType(avatar.getMediaType());
                    existingAvatar.setData(avatar.getData());
                    existingAvatar.setStudent(avatar.getStudent());
                    return avatarRepository.save(existingAvatar);
                })
                .orElse(null);
    }

    public void deleteAvatar(Long id) {
        avatarRepository.deleteById(id);
    }

    public List<Avatar> getAllAvatars() {
        return avatarRepository.findAll();
    }

    public Avatar uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Создаем директорию если не существует
        createAvatarsDirectory();

        // Генерируем путь для сохранения файла
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String fileName = "student_" + studentId + fileExtension;
        Path filePath = Path.of(avatarsDirectory, fileName);

        // Сохраняем файл на диск
        Files.write(filePath, file.getBytes());

        // Создаем превью
        byte[] preview = generatePreview(file);

        // Создаем или обновляем аватар
        Avatar avatar = avatarRepository.findByStudentId(studentId).orElse(new Avatar());
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(preview); // Сохраняем превью в БД

        return avatarRepository.save(avatar);
    }

    public byte[] getAvatarFromDb(Long studentId) {
        Avatar avatar = avatarRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Avatar not found in database"));
        return avatar.getData();
    }

    public byte[] getAvatarFromDisk(Long studentId) throws IOException {
        Avatar avatar = avatarRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Avatar not found"));

        Path filePath = Path.of(avatar.getFilePath());
        if (!Files.exists(filePath)) {
            throw new RuntimeException("File not found on disk: " + filePath);
        }

        return Files.readAllBytes(filePath);
    }

    private void createAvatarsDirectory() {
        try {
            Files.createDirectories(Path.of(avatarsDirectory));
            System.out.println("Avatars directory created: " + avatarsDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create avatars directory: " + avatarsDirectory, e);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    private byte[] generatePreview(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            BufferedImage originalImage = ImageIO.read(is);
            if (originalImage == null) {
                return file.getBytes(); // Если не изображение, возвращаем оригинал
            }

            // Создаем превью 100x100
            BufferedImage previewImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = previewImage.createGraphics();
            graphics.drawImage(originalImage, 0, 0, 100, 100, null);
            graphics.dispose();

            // Конвертируем в byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(previewImage, "jpg", baos);
            return baos.toByteArray();
        }
    }
}
