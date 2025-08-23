package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/avatars")
@Tag(name = "Avatar Controller", description = "Управление аватарами студентов")
public class AvatarController {
    private final AvatarService avatarService;


    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @Operation(summary = "Создать аватар")
    @PostMapping
    public ResponseEntity<Avatar> createAvatar(@RequestBody Avatar avatar) {
        Avatar createdAvatar = avatarService.createAvatar(avatar);
        return ResponseEntity.ok(createdAvatar);
    }

    @Operation(summary = "Получить аватар по ID")
    @GetMapping("/{id}")
    public ResponseEntity<Avatar> getAvatarById(
            @Parameter(description = "ID аватара") @PathVariable Long id) {
        Avatar avatar = avatarService.getAvatarById(id);
        if (avatar == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(avatar);
    }

    @Operation(summary = "Получить аватар по ID студента")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<Avatar> getAvatarByStudentId(
            @Parameter(description = "ID студента") @PathVariable Long studentId) {
        Avatar avatar = avatarService.getAvatarByStudentId(studentId);
        if (avatar == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(avatar);
    }

    @Operation(summary = "Обновить аватар")
    @PutMapping("/{id}")
    public ResponseEntity<Avatar> updateAvatar(
            @Parameter(description = "ID аватара") @PathVariable Long id,
            @RequestBody Avatar avatar) {
        Avatar updatedAvatar = avatarService.updateAvatar(id, avatar);
        if (updatedAvatar == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedAvatar);
    }

    @Operation(summary = "Удалить аватар")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvatar(
            @Parameter(description = "ID аватара") @PathVariable Long id) {
        avatarService.deleteAvatar(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить все аватары")
    @GetMapping
    public ResponseEntity<List<Avatar>> getAllAvatars() {
        List<Avatar> avatars = avatarService.getAllAvatars();
        return ResponseEntity.ok(avatars);
    }

    @Operation(summary = "Загрузить аватар для студента")
    @PostMapping(value = "/upload/{studentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Avatar> uploadAvatar(
            @Parameter(description = "ID студента") @PathVariable Long studentId,
            @Parameter(description = "Файл аватара") @RequestParam MultipartFile file) {
        try {
            Avatar avatar = avatarService.uploadAvatar(studentId, file);
            return ResponseEntity.ok(avatar);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Получить аватар из БД (превью)")
    @GetMapping(value = "/db/{studentId}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<byte[]> getAvatarFromDb(
            @Parameter(description = "ID студента") @PathVariable Long studentId) {
        try {
            byte[] avatarData = avatarService.getAvatarFromDb(studentId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(avatarData, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Получить аватар с диска (оригинал)")
    @GetMapping(value = "/disk/{studentId}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<byte[]> getAvatarFromDisk(
            @Parameter(description = "ID студента") @PathVariable Long studentId) {
        try {
            byte[] avatarData = avatarService.getAvatarFromDisk(studentId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(avatarData, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}