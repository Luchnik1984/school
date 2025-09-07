package ru.hogwarts.school.dto;

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
}

