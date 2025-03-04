package no.hiof.examScheduling.model;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model class representing an exam with simplified date handling
 */
public class Exam {
    private String courseCode;
    private String courseName;
    private String examType;
    private String honorar;
    private String platform;
    private LocalDateTime examDate;     // Start date of the exam
    private LocalDateTime examDateEnd;  // End date for multi-day exams
    private String responsible;
    private String internalSensor;
    private String internalSensor2;
    private String externalSensor;
    private String honorar2;
    private String comment;

    // Default constructor
    public Exam() {
    }

    // Constructor with parameters
    public Exam(String courseCode, String courseName, String examType, String honorar,
                String platform, LocalDateTime examDate, LocalDateTime examDateEnd,
                String responsible, String internalSensor, String externalSensor, String comment) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.examType = examType;
        this.honorar = honorar;
        this.platform = platform;
        this.examDate = examDate;
        this.examDateEnd = examDateEnd;
        this.responsible = responsible;
        this.internalSensor = internalSensor;
        this.externalSensor = externalSensor;
        this.comment = comment;
    }

    // Getters and setters
    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public String getHonorar() {
        return honorar;
    }

    public void setHonorar(String honorar) {
        this.honorar = honorar;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public LocalDateTime getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDateTime examDate) {
        this.examDate = examDate;
    }

    public LocalDateTime getExamDateEnd() {
        return examDateEnd;
    }

    public void setExamDateEnd(LocalDateTime examDateEnd) {
        this.examDateEnd = examDateEnd;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public String getInternalSensor() {
        return internalSensor;
    }

    public void setInternalSensor(String internalSensor) {
        this.internalSensor = internalSensor;
    }

    public String getInternalSensor2() {
        return internalSensor2;
    }

    public void setInternalSensor2(String internalSensor2) {
        this.internalSensor2 = internalSensor2;
    }

    public String getExternalSensor() {
        return externalSensor;
    }

    public void setExternalSensor(String externalSensor) {
        this.externalSensor = externalSensor;
    }

    public String getHonorar2() {
        return honorar2;
    }

    public void setHonorar2(String honorar2) {
        this.honorar2 = honorar2;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Formats the exam date as a string
     */
    public String getFormattedExamDate() {
        if (examDate == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return examDate.format(formatter);
    }

    /**
     * Formats the exam end date as a string
     */
    public String getFormattedExamDateEnd() {
        if (examDateEnd == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return examDateEnd.format(formatter);
    }

    // Enhance Exam class to better handle date ranges
    public String getFormattedDateRange() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        if (examDate == null) {
            return examDateEnd != null ? "Until " + examDateEnd.format(formatter) : "";
        } else if (examDateEnd == null) {
            return "From " + examDate.format(formatter);
        } else {
            return examDate.format(formatter) + " - " + examDateEnd.format(formatter);
        }
    }

    @Override
    public String toString() {
        return courseCode + " - " + courseName + getFormattedExamDate() + "-" + getFormattedExamDateEnd();
    }
}
