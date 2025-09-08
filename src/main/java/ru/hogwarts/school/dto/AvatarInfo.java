package ru.hogwarts.school.dto;

import java.util.Objects;

public class AvatarInfo {
    private Long id;
    private Long studentId;
    private String studentName;
    private String filePath;
    private long fileSize;
    private String mediaType;

    public AvatarInfo(Long id, Long studentId, String studentName,
                      String filePath, long fileSize, String mediaType) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.mediaType = mediaType;
    }

    public AvatarInfo() {

    }


    public Long getId() { return id; }
    public Long getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getFilePath() { return filePath; }
    public long getFileSize() { return fileSize; }
    public String getMediaType() { return mediaType; }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvatarInfo that = (AvatarInfo) o;
        return fileSize == that.fileSize &&
                Objects.equals(id, that.id) &&
                Objects.equals(filePath, that.filePath) &&
                Objects.equals(mediaType, that.mediaType) &&
                Objects.equals(studentId, that.studentId) &&
                Objects.equals(studentName, that.studentName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, filePath, fileSize, mediaType, studentId, studentName);
    }

    // toString для удобства отладки
    @Override
    public String toString() {
        return "AvatarInfo{" +
                "id=" + id +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", mediaType='" + mediaType + '\'' +
                ", studentId=" + studentId +
                ", studentName='" + studentName + '\'' +
                '}';
    }

}

