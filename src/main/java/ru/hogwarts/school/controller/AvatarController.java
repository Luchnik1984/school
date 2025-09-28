package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.dto.AvatarInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/avatar")
@Tag(name = "Avatar Controller", description = "Управление аватарами студентов")
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @Operation(summary = "Загрузить аватар для студента")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аватар успешно загружен"),
            @ApiResponse(responseCode = "404", description = "Студент не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка при загрузке файла")
    })
    @PostMapping(value = "/{studentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(
            @Parameter(description = "ID студента") @PathVariable Long studentId,
            @Parameter(description = "Файл аватара") @RequestParam MultipartFile avatarFile) {
        try {
            avatarService.uploadAvatar(studentId, avatarFile);
            return ResponseEntity.ok("Avatar uploaded successfully for student ID: " + studentId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload avatar: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Student not found: " + e.getMessage());
        }
    }

    @Operation(summary = "Получить превью аватара из БД")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Превью успешно получено"),
            @ApiResponse(responseCode = "404", description = "Аватар не найден")
    })
    @GetMapping(value = "/{id}/preview")
    public ResponseEntity<byte[]> getAvatarPreview(
            @Parameter(description = "ID студента") @PathVariable Long id) {
        try {
            byte[] previewData = avatarService.getAvatarDataFromDatabase(id);
            if (previewData == null || previewData.length == 0) {
                return ResponseEntity.notFound().build();
            }
            Avatar avatar = avatarService.findAvatar(id);

            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", avatar.getMediaType())
                    .header("Content-Length", String.valueOf(previewData.length))
                    .body(previewData);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Получить оригинальный аватар с диска")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Оригинальный аватар успешно получено"),
            @ApiResponse(responseCode = "404", description = "Аватар не найден")
    })
    @GetMapping(value = "/{id}/avatar-from-file")
    public void getOriginalAvatar(
            @Parameter(description = "ID студента") @PathVariable Long id,
            HttpServletResponse response) throws IOException {
        try {
            Avatar avatar = avatarService.getAvatarFromDisk(id);
            Path path = Path.of(avatar.getFilePath());

            try (InputStream is = Files.newInputStream(path);
                 OutputStream os = response.getOutputStream()) {

                response.setStatus(200);
                response.setContentType(avatar.getMediaType());
                response.setContentLength((int) avatar.getFileSize());

                is.transferTo(os);
            }
        } catch (IOException | RuntimeException e) {
            response.setStatus(404);
            response.getWriter().write("Avatar not found: " + e.getMessage());
        }
    }

    @Operation(summary = "Удалить аватар")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аватар успешно удален"),
            @ApiResponse(responseCode = "404", description = "Аватар не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка при удалении файла")
    })
    @DeleteMapping("/{studentId}")
    public ResponseEntity<String> deleteAvatar(
            @Parameter(description = "ID студента") @PathVariable Long studentId) {
        try {
            avatarService.deleteAvatar(studentId);
            return ResponseEntity.ok("Avatar deleted successfully for student ID: " + studentId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete avatar file: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Avatar not found: " + e.getMessage());
        }
    }

    @Operation(summary = "Получить информацию об аватарах с пагинацией")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Страница с информацией об аватарах"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры пагинации")
    })
    @GetMapping("/page")
    public ResponseEntity<Page<AvatarInfo>> getAvatarsPage(
            @Parameter(description = "Номер страницы (начинается с 1)", example = "1")
            @RequestParam(defaultValue = "1") Integer page,

            @Parameter(description = "Размер страницы", example = "3")
            @RequestParam(defaultValue = "3") Integer size) {

        if (page < 1 || size <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Page<AvatarInfo> avatarsPage = avatarService.getAvatarsInfo(page-1, size);
        return ResponseEntity.ok(avatarsPage);
    }

}

