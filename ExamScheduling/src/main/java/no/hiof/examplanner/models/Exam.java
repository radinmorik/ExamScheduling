package no.hiof.examplanner.models;

import java.time.LocalDateTime;

public class Exam {
    private String courseCode;
    private String courseName;
    private String examType;
    private LocalDateTime examDate;
    private String responsible;
    private String internalSensor;
    private String externalSensor;

    public Exam(String courseCode, String courseName, String examType, LocalDateTime examDate,
                String responsible, String internalSensor, String externalSensor) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.examType = examType;
        this.examDate = examDate;
        this.responsible = responsible;
        this.internalSensor = internalSensor;
        this.externalSensor = externalSensor;
    }

    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public String getExamType() { return examType; }
    public LocalDateTime getExamDate() { return examDate; }
    public String getResponsible() { return responsible; }
    public String getInternalSensor() { return internalSensor; }
    public String getExternalSensor() { return externalSensor; }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s on %s", courseCode, courseName, examType, examDate);
    }
}
