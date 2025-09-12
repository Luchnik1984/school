package ru.hogwarts.school.mapper;

import org.springframework.stereotype.Component;
import ru.hogwarts.school.dto.AvatarInfo;
import ru.hogwarts.school.model.Avatar;

@Component
public class AvatarMapper {

    public AvatarInfo toAvatarInfo(Avatar avatar) {
        if (avatar == null) {
            return null;
        }

        AvatarInfo info = new AvatarInfo();
        info.setId(avatar.getId());
        info.setFilePath(avatar.getFilePath());
        info.setFileSize(avatar.getFileSize());
        info.setMediaType(avatar.getMediaType());

        // Безопасное получение данных студента
        if (avatar.getStudent() != null) {
            info.setStudentId(avatar.getStudent().getId());
            info.setStudentName(avatar.getStudent().getName());
        }

        return info;
    }
}
