package no.hiof.examplanner.models;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Exam {
    private String courseCode;
    private String courseName;
    private String examType;
    private String honorar;
    private String platform;
    private List<LocalDateTime> examDates; // Now stores multiple dates instead of a single one
    private String responsible;
    private String internalSensor;
    private String internalSensor2;
    private String externalSensor;
    private String honorar2;
    private String comment;

    public Exam(String courseCode, String courseName, String examType, String honorar, String platform,
                List<LocalDateTime> examDates, String responsible, String internalSensor,
                String internalSensor2, String externalSensor, String honorar2, String comment) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.examType = examType;
        this.honorar = honorar;
        this.platform = platform;
        this.examDates = examDates;
        this.responsible = responsible;
        this.internalSensor = internalSensor;
        this.internalSensor2 = internalSensor2;
        this.externalSensor = externalSensor;
        this.honorar2 = honorar2;
        this.comment = comment;
    }

    public Exam() {
        this.examDates = new ArrayList<>(); // Initialize empty list to avoid null errors
    }


    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedDates = (examDates.isEmpty()) ? "N/A" :
                examDates.stream().map(d -> d.format(formatter)).reduce((d1, d2) -> d1 + ", " + d2).orElse("");

        return "Exam{" +
                "courseCode='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", examType='" + examType + '\'' +
                ", honorar='" + honorar + '\'' +
                ", platform='" + platform + '\'' +
                ", examDates=[" + formattedDates + "]" +
                ", responsible='" + responsible + '\'' +
                ", internalSensor='" + internalSensor + '\'' +
                ", internalSensor2='" + internalSensor2 + '\'' +
                ", externalSensor='" + externalSensor + '\'' +
                ", honorar2='" + honorar2 + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }

    /**
     * Parses a date string from the CSV and returns a list of LocalDateTime objects.
     */
    public static List<LocalDateTime> parseExamDates(String dateString) {
        List<LocalDateTime> dates = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.MM.yyyy HH:mm");

        if (dateString == null || dateString.isEmpty()) {
            return dates; // Return empty list if there's no date info
        }

        // Handle date range (e.g., "9-10.12.2024")
        if (dateString.matches("\\d+-\\d+\\.\\d+\\.\\d{4}")) {
            String[] parts = dateString.split("[-.]");
            int startDay = Integer.parseInt(parts[0]);
            int endDay = Integer.parseInt(parts[1]);
            int month = Integer.parseInt(parts[2]);
            int year = Integer.parseInt(parts[3]);

            for (int day = startDay; day <= endDay; day++) {
                dates.add(LocalDate.of(year, month, day).atStartOfDay());
            }
            return dates;
        }

        // Handle single date with time (e.g., "16.12.2024 14:00")
        try {
            dates.add(LocalDateTime.parse(dateString, formatter));
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format: " + dateString);
        }

        return dates;
    }

    // Getters and Setters
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }

    public String getHonorar() { return honorar; }
    public void setHonorar(String honorar) { this.honorar = honorar; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public List<LocalDateTime> getExamDates() { return examDates; }
    public void setExamDates(List<LocalDateTime> examDates) { this.examDates = examDates; }

    public String getResponsible() { return responsible; }
    public void setResponsible(String responsible) { this.responsible = responsible; }

    public String getInternalSensor() { return internalSensor; }
    public void setInternalSensor(String internalSensor) { this.internalSensor = internalSensor; }

    public String getInternalSensor2() { return internalSensor2; }
    public void setInternalSensor2(String internalSensor2) { this.internalSensor2 = internalSensor2; }

    public String getExternalSensor() { return externalSensor; }
    public void setExternalSensor(String externalSensor) { this.externalSensor = externalSensor; }

    public String getHonorar2() { return honorar2; }
    public void setHonorar2(String honorar2) { this.honorar2 = honorar2; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
