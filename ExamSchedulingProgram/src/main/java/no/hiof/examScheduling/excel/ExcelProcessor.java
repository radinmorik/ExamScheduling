package no.hiof.examScheduling.excel;


import no.hiof.examScheduling.model.Exam;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Core Excel processing functionality shared across the application.
 * Separates parsing logic from business logic.
 */
public class ExcelProcessor {
    private static final Logger LOGGER = Logger.getLogger(ExcelProcessor.class.getName());

    // Common formatters and constants
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final DateTimeFormatter[] DATE_FORMATTERS = {
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"),
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("d.MM.yyyy HH:mm"),
            DateTimeFormatter.ofPattern("d.MM.yyyy")
    };

    public static final int COL_COURSE_CODE = 0;
    public static final int COL_COURSE_NAME = 1;
    public static final int COL_EXAM_TYPE = 2;
    public static final int COL_HONORAR = 3;
    public static final int COL_PLATFORM = 4;
    public static final int COL_DATE_FROM = 6;  // Empty column
    public static final int COL_DATE_TO = 5;    // Contains start date
    public static final int COL_RESPONSIBLE = 7;
    public static final int COL_INTERNAL_SENSOR = 8;
    public static final int COL_INTERNAL_SENSOR2 = 9;
    public static final int COL_EXTERNAL_SENSOR = 10;
    public static final int COL_HONORAR2 = 12;
    public static final int COL_COMMENT = 13;

    /**
     * Creates an Exam object from an Excel row using standardized column mapping
     */
    public static Exam createExamFromRow(Row row) {
        if (row == null) return null;

        // Extract data from cells
        String courseCode = getCellValueAsString(row.getCell(COL_COURSE_CODE));

        // Skip invalid rows
        if (!isValidCourseCode(courseCode)) {
            return null;
        }

        String courseName = getCellValueAsString(row.getCell(COL_COURSE_NAME));
        String examType = getCellValueAsString(row.getCell(COL_EXAM_TYPE));
        String honorar = getCellValueAsString(row.getCell(COL_HONORAR));
        String platform = getCellValueAsString(row.getCell(COL_PLATFORM));
        String dateFromString = getCellValueAsString(row.getCell(COL_DATE_FROM));
        String dateToString = getCellValueAsString(row.getCell(COL_DATE_TO));
        String responsible = getCellValueAsString(row.getCell(COL_RESPONSIBLE));
        String internalSensor = getCellValueAsString(row.getCell(COL_INTERNAL_SENSOR));
        String internalSensor2 = getCellValueAsString(row.getCell(COL_INTERNAL_SENSOR2));
        String externalSensor = getCellValueAsString(row.getCell(COL_EXTERNAL_SENSOR));
        String honorar2 = getCellValueAsString(row.getCell(COL_HONORAR2));
        String comment = getCellValueAsString(row.getCell(COL_COMMENT));

        // Parse dates
        LocalDateTime examDate = parseDateTime(dateFromString);
        LocalDateTime examDateEnd = parseDateTime(dateToString);

        // Log warnings for date parsing failures
        if (examDate == null && !dateFromString.isEmpty()) {
            LOGGER.warning("Failed to parse exam date: " + dateFromString + " for course " + courseCode);
        }
        if (examDateEnd == null && !dateToString.isEmpty()) {
            LOGGER.warning("Failed to parse exam end date: " + dateToString + " for course " + courseCode);
        }

        // Create and populate the exam object
        Exam exam = new Exam();
        exam.setCourseCode(courseCode);
        exam.setCourseName(courseName);
        exam.setExamType(examType);
        exam.setHonorar(honorar);
        exam.setPlatform(platform);
        exam.setExamDate(examDate);
        exam.setExamDateEnd(examDateEnd);
        exam.setResponsible(responsible);
        exam.setInternalSensor(internalSensor);
        exam.setInternalSensor2(internalSensor2);
        exam.setExternalSensor(externalSensor);
        exam.setHonorar2(honorar2);
        exam.setComment(comment);

        System.out.println(exam.getExamDateEnd());
        // getExamDateEnd gir null i resultat.
        // mappingen her er feil et eller annet sted

        /*
            public static final int COL_DATE_FROM = 6;  // Empty column
            public static final int COL_DATE_TO = 5;



           problemet ligger nok i excelprocessor og examPanel



                private void addExamToTable(Exam exam) {
        tableModel.addRow(new Object[] {
                exam.getCourseCode(),         // 0: Emnekode
                exam.getCourseName(),         // 1: Emnenavn
                exam.getExamType(),           // 2: Vurderingsform
                exam.getHonorar(),            // 3: Honorar
                exam.getPlatform(),           // 4: Platform
                exam.getFormattedExamDate(),  // 6: Dato til (putting the start date here)
                exam.getResponsible(),        // 7: Ansvarlig
                exam.getInternalSensor(),     // 8: Intern sensor
                exam.getInternalSensor2(),    // 9: Intern sensor 2
                exam.getExternalSensor(),
                "",// 10: Ekstern sensor
                exam.getHonorar2(),// 11: Honorar 2
                exam.getComment()             // 12: Kommentar

        });

        //tableModel.addRow(rowData);
    }



         */

        return exam;
    }

    /**
     * Checks if a string is a valid HIOF course code format
     */
    public static boolean isValidCourseCode(String code) {
        if (code == null || code.trim().isEmpty()) return false;
        // Match patterns like ITF10214, ITF20335, etc.
        return code.matches("^[A-Z]{2,3}\\d{5}$");
    }

    /**
     * Parses a date string into a LocalDateTime, trying multiple formats
     */
    public static LocalDateTime parseDateTime(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        // Clean the string and prepare for parsing
        dateString = dateString.trim();

        // Add default time if only date is provided
        if (!dateString.contains(":") && dateString.contains(".")) {
            dateString += " 00:00";
        }

        // Try each formatter in sequence
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDateTime.parse(dateString, formatter);
            } catch (DateTimeParseException e) {
                // Continue to the next formatter
            }
        }

        LOGGER.fine("Could not parse date: " + dateString);
        return null;
    }

    /**
     * Gets cell value as a string regardless of the cell type
     */
    public static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue().trim();

                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        try {
                            return DATE_FORMATTER.format(cell.getLocalDateTimeCellValue());
                        } catch (Exception e) {
                            LOGGER.fine("Error formatting date cell: " + e.getMessage());
                            return String.valueOf(cell.getNumericCellValue());
                        }
                    } else {
                        double value = cell.getNumericCellValue();
                        return value == Math.floor(value) ?
                                String.valueOf((int) value) :
                                String.valueOf(value);
                    }

                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());

                case FORMULA:
                    try {
                        // First try to get the formula result directly
                        CellType cachedType = cell.getCachedFormulaResultType();
                        if (cachedType == CellType.NUMERIC) {
                            if (DateUtil.isCellDateFormatted(cell)) {
                                return DATE_FORMATTER.format(cell.getLocalDateTimeCellValue());
                            } else {
                                double value = cell.getNumericCellValue();
                                return value == Math.floor(value) ?
                                        String.valueOf((int) value) :
                                        String.valueOf(value);
                            }
                        } else if (cachedType == CellType.STRING) {
                            return cell.getStringCellValue();
                        } else if (cachedType == CellType.BOOLEAN) {
                            return String.valueOf(cell.getBooleanCellValue());
                        }
                        return "";
                    } catch (Exception e) {
                        LOGGER.warning("Error evaluating formula: " + e.getMessage());
                        return "";
                    }

                case BLANK:
                case ERROR:
                default:
                    return "";
            }
        } catch (Exception e) {
            LOGGER.warning("Error getting cell value: " + e.getMessage());
            return "";
        }
    }

    /**
     * Truncates a string to the specified length with ellipsis if needed
     */
    public static String truncate(String str, int maxLength) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        return str.length() <= maxLength ? str : str.substring(0, maxLength - 3) + "...";
    }
}
