package ru.hogwarts.school.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.AvatarInfo;
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
import java.awt.RenderingHints;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AvatarService {
    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    @Value("${avatar.directory.path}")
    private String avatarsDir;

    // Константа для ширины превью
    private static final int PREVIEW_WIDTH = 100;
    private static final int STREAM_BUFFER_SIZE = 1024;

    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    public void uploadAvatar(Long studentId, MultipartFile avatarFile) throws IOException {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        Path filePath = Path.of(avatarsDir, studentId + "." + getExtensions(avatarFile.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (
                InputStream is = avatarFile.getInputStream();
                OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                BufferedInputStream bis = new BufferedInputStream(is, STREAM_BUFFER_SIZE);
                BufferedOutputStream bos = new BufferedOutputStream(os, STREAM_BUFFER_SIZE)
        ) {
            bis.transferTo(bos);
        }
        // Генерируем превью для БД
        byte[] previewData = generatePreviewData(filePath);

        Avatar avatar = findOrCreateAvatar(studentId);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(avatarFile.getSize());
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setData(previewData); // Сохраняем ПРЕВЬЮ в БД, а не оригинал

        avatarRepository.save(avatar);
    }

    /**
     * Генерирует уменьшенное превью изображения для хранения в БД
     */
    private byte[] generatePreviewData(Path filePath) throws IOException {
        try (
                InputStream is = Files.newInputStream(filePath);
                BufferedInputStream bis = new BufferedInputStream(is, STREAM_BUFFER_SIZE);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ) {
            // Читаем оригинальное изображение
            BufferedImage originalImage = ImageIO.read(bis);

            if (originalImage == null) {
                // Если это не изображение, возвращаем пустой массив
                return new byte[0];
            }

            // Рассчитываем высоту пропорционально ширине
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            // Защита от деления на ноль
            if (originalWidth == 0) {
                return new byte[0];
            }

            int previewHeight = originalHeight * PREVIEW_WIDTH / originalWidth;

            // Создаем превью
            BufferedImage previewImage = new BufferedImage(
                    PREVIEW_WIDTH,
                    previewHeight,
                    originalImage.getType()

            );

            // Масштабируем изображение
            Graphics2D graphics = previewImage.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.drawImage(originalImage, 0, 0, PREVIEW_WIDTH, previewHeight, null);
            graphics.dispose();

            // Сохраняем превью в байтовый массив
            String formatName = getExtensions(filePath.getFileName().toString());
            ImageIO.write(previewImage, formatName, baos);

            return baos.toByteArray();
        }
    }

    public Avatar findAvatar(Long studentId) {
        return avatarRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Avatar not found for student id: " + studentId));
    }

    public byte[] getAvatarDataFromDatabase(Long studentId) {
        Avatar avatar = findAvatar(studentId);
        return avatar.getData(); // Возвращаем превью из БД
    }

    public Avatar getAvatarFromDisk(Long studentId) throws IOException {
        Avatar avatar = findAvatar(studentId);
        Path path = Path.of(avatar.getFilePath());

        if (!Files.exists(path)) {
            throw new IOException("Avatar file not found on disk: " + path);
        }

        return avatar;
    }

    public void deleteAvatar(Long studentId) throws IOException {
        Avatar avatar = findAvatar(studentId);
        Path filePath = Path.of(avatar.getFilePath());

        Files.deleteIfExists(filePath);
        avatarRepository.delete(avatar);
    }

    private Avatar findOrCreateAvatar(Long studentId) {
        return avatarRepository.findByStudentId(studentId)
                .orElse(new Avatar());
    }

    private String getExtensions(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public Page<Avatar> getAllAvatarsWithPagination(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return avatarRepository.findAll(pageable);
    }

    // Метод для получения информации об аватарах (без данных файлов)
    public Page<AvatarInfo> getAvatarsInfo(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Avatar> avatarsPage = avatarRepository.findAll(pageable);

        return avatarsPage.map(avatar -> new AvatarInfo(
                avatar.getId(),
                avatar.getStudent().getId(),
                avatar.getStudent().getName(),
                avatar.getFilePath(),
                avatar.getFileSize(),
                avatar.getMediaType()
        ));
    }
}


