package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.AvatarInfo;
import ru.hogwarts.school.mapper.AvatarMapper;
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
    private final AvatarMapper avatarMapper;
    private final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    @Value("${avatar.directory.path}")
    private String avatarsDir;

    // Константа для ширины превью
    private static final int PREVIEW_WIDTH = 100;
    private static final int STREAM_BUFFER_SIZE = 1024;

    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository, AvatarMapper avatarMapper) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
        this.avatarMapper = avatarMapper;
    }

    public void uploadAvatar(Long studentId, MultipartFile avatarFile) throws IOException {
        logger.info("Was invoked method for upload avatar for student id = {}", studentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->{
                    logger.error("Student with id {} not found", studentId);
                return new RuntimeException("Student not found with id: " + studentId);}
                );

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
        logger.debug("Generating preview for avatar file: {}", filePath);
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
        logger.info("Was invoked metod for find avatar for student id = {}", studentId);
        return avatarRepository.findByStudentId(studentId)
                .orElseThrow(() -> {
                    logger.error("Avatar not found for student id = {}", studentId);
                     return new RuntimeException("Avatar not found for student id: " + studentId);
                });
    }

    public byte[] getAvatarDataFromDatabase(Long studentId) {
        logger.info("Was invoked method for get avatar data from database for student id = {}", studentId);
        Avatar avatar = findAvatar(studentId);
        return avatar.getData(); // Возвращаем превью из БД
    }

    public Avatar getAvatarFromDisk(Long studentId) throws IOException {
        logger.info("Was invoked method for get avatar from disk for student id = {}", studentId);
        Avatar avatar = findAvatar(studentId);
        Path path = Path.of(avatar.getFilePath());

        if (!Files.exists(path)) {
            logger.error("Avatar not found on disk: {}", path);
            throw new IOException("Avatar file not found on disk: " + path);
        }

        return avatar;
    }

    public void deleteAvatar(Long studentId) throws IOException {
        logger.info("Was invoked method for delete avatar for student id = {}", studentId);
        Avatar avatar = findAvatar(studentId);
        Path filePath = Path.of(avatar.getFilePath());

        Files.deleteIfExists(filePath);
        avatarRepository.delete(avatar);
        logger.debug("Avatar deleted successfully for student id = {}", studentId);
    }

    private Avatar findOrCreateAvatar(Long studentId) {
        logger.debug("Finding or creating avatar for student id = {}", studentId);
        return avatarRepository.findByStudentId(studentId)
                .orElse(new Avatar());
    }

    private String getExtensions(String fileName) {
        logger.debug("Getting file extension for file: {}", fileName);
        String extension = StringUtils.getFilenameExtension(fileName);
        logger.debug("File extension determined as: {}", extension);
        return extension;
    }

    // Метод для получения информации об аватарах (без данных файлов)
    public Page<AvatarInfo> getAvatarsInfo(int pageNumber, int pageSize) {
        logger.info("Was invoked method for get avatars info - page: {}, size: {}", pageNumber, pageSize);

        if (pageNumber < 0) {
            logger.warn("Invalid page number: {} - cannot be negative", pageNumber);
            pageNumber = 0; // Корректируем на первую страницу
        }

        if (pageSize <= 0) {
            logger.warn("Invalid page size: {} - must be positive", pageSize);
            pageSize = 10; // Устанавливаем размер по умолчанию
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Avatar> avatarsPage = avatarRepository.findAll(pageable);

        logger.debug("Retrieved {} avatars from database for page {}",
                avatarsPage.getNumberOfElements(), pageNumber);
        logger.info("Total avatars in system: {}, total pages: {}",
                avatarsPage.getTotalElements(), avatarsPage.getTotalPages());

        Page<AvatarInfo> resultPage = avatarsPage.map(avatar -> new AvatarInfo(
                avatar.getId(),
                avatar.getStudent().getId(),
                avatar.getStudent().getName(),
                avatar.getFilePath(),
                avatar.getFileSize(),
                avatar.getMediaType()
        ));
        logger.debug("Successfully mapped {} avatars to AvatarInfo objects",
                resultPage.getNumberOfElements());
        return resultPage;
    }
}


