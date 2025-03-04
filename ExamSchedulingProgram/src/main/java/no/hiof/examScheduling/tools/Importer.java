package no.hiof.examScheduling.tools;


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
 * The Importer class handles reading and parsing Excel files containing exam data.
 * Simplified with clearer date handling.
 */
public class Importer {
    // Logger for error tracking
    private static final Logger LOGGER = Logger.getLogger(Importer.class.getName());

    // Constants for configuration
    private static final int DATA_START_ROW = 12; // Start row in Excel file
    private static final String CSV_DELIMITER = ",";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    // Instance variables
    private final Workbook workbook;
    private final Sheet sheet;
    private final List<Exam> exams;

    /**
     * Constructor that opens the Excel file and prepares for importing
     *
     * @param excelFile The Excel file to import
     * @throws IOException If there's an error reading the file
     */
    public Importer(File excelFile) throws IOException {
        this.workbook = WorkbookFactory.create(excelFile);
        this.sheet = workbook.getSheetAt(0);
        this.exams = new ArrayList<>();
    }

    /**
     * Main method to read the Excel file and create Exam objects
     */
    public void importExcelToObjects() {
        LOGGER.info("Starting import process...");
        int rowNum = 0;
        int successCount = 0;
        int errorCount = 0;

        try {
            for (Row row : sheet) {
                rowNum++;

                // Skip header rows
                if (rowNum < DATA_START_ROW) {
                    continue;
                }

                // Check if this row contains a valid course code
                Cell firstCell = row.getCell(0);
                if (firstCell == null) {
                    continue;  // Skip empty rows
                }

                String cellValue = getCellValueAsString(firstCell);
                if (cellValue.trim().isEmpty()) {
                    continue;  // Skip empty rows
                }

                // Validate course code format (e.g., ITF10214)
                if (!isHiofCourseCode(cellValue)) {
                    LOGGER.fine("Skipping row " + rowNum + ": Not a course code: " + cellValue);
                    continue;
                }

                try {
                    Exam exam = createExamFromRow(row);
                    if (exam != null) {
                        exams.add(exam);
                        successCount++;
                        LOGGER.info("Row " + rowNum + ": Successfully imported: " + exam.getCourseCode() + " - " + exam.getCourseName());
                    }
                } catch (Exception e) {
                    errorCount++;
                    LOGGER.log(Level.WARNING, "Error processing row " + rowNum + ": " + e.getMessage(), e);
                }
            }

            LOGGER.info("Import completed. Successfully imported: " + successCount + ", Errors: " + errorCount);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Critical error during import", e);
        }
    }

    /**
     * Checks if a string is a valid HIOF course code
     */
    private boolean isHiofCourseCode(String code) {
        if (code == null) return false;
        // Match patterns like ITF10214, ITF20335, etc.
        return code.matches("^[A-Z]{2,3}\\d{5}$");
    }

    /**
     * Updates for the createExamFromRow method in both Importer and ExcelService classes
     */
    private Exam createExamFromRow(Row row) {
        // Extract data from cells based on Excel structure
        String courseCode = getCellValueAsString(row.getCell(0));
        String courseName = getCellValueAsString(row.getCell(1));
        String examType = getCellValueAsString(row.getCell(2));
        String honorar = getCellValueAsString(row.getCell(3));
        String platform = getCellValueAsString(row.getCell(4));

        // Handle the date columns with special attention
        String dateFromString = getCellValueAsString(row.getCell(5));
        String dateToString = getCellValueAsString(row.getCell(6));

        String responsible = getCellValueAsString(row.getCell(7));
        String internalSensor = getCellValueAsString(row.getCell(8));
        String internalSensor2 = getCellValueAsString(row.getCell(9));
        String externalSensor = getCellValueAsString(row.getCell(10));
        String honorar2 = getCellValueAsString(row.getCell(11));
        String comment = getCellValueAsString(row.getCell(12));

        // Debug logging to help identify issues
        LOGGER.fine("Row data: " + courseCode + ", Date From: " + dateFromString +
                ", Date To: " + dateToString + ", Comment: " + comment);

        // Parse dates with enhanced parser
        LocalDateTime examDate = parseDateTime(dateFromString);
        LocalDateTime examDateEnd = parseDateTime(dateToString);

        // Create and return the exam object
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

        // Add verification to ensure data was mapped correctly
        if (examDate == null && !dateFromString.isEmpty()) {
            LOGGER.warning("Failed to parse exam date: " + dateFromString + " for course " + courseCode);
        }
        if (examDateEnd == null && !dateToString.isEmpty()) {
            LOGGER.warning("Failed to parse exam end date: " + dateToString + " for course " + courseCode);
        }

        return exam;
    }

    /**
     * Enhanced date parser that handles multiple date formats and cell types
     * Add this to both the Importer and ExcelService classes
     */
    private LocalDateTime parseDateTime(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        // Clean the string and prepare for parsing
        dateString = dateString.trim();

        // Try multiple date formats
        DateTimeFormatter[] formatters = {
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"),
                DateTimeFormatter.ofPattern("dd.MM.yyyy"),
                DateTimeFormatter.ofPattern("d.MM.yyyy HH:mm"),
                DateTimeFormatter.ofPattern("d.MM.yyyy")
        };

        // If the date string has only a date part, add time
        if (!dateString.contains(":") && dateString.contains(".")) {
            dateString += "";
        }

        // Try each formatter
        for (DateTimeFormatter formatter : formatters) {
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
     * Enhanced cell value getter that handles date cells better
     * Replace the existing getCellValueAsString method in both classes
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue().trim();

                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        // Handle date cells directly
                        try {
                            // Get as LocalDateTime to preserve both date and time
                            return DATE_FORMATTER.format(cell.getLocalDateTimeCellValue());
                        } catch (Exception e) {
                            // Fallback to raw numeric value if date conversion fails
                            LOGGER.fine("Error formatting date cell: " + e.getMessage());
                            return String.valueOf(cell.getNumericCellValue());
                        }
                    } else {
                        // Format numbers without decimal for integer values
                        double value = cell.getNumericCellValue();
                        return value == Math.floor(value) ?
                                String.valueOf((int) value) :
                                String.valueOf(value);
                    }

                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());

                case FORMULA:
                    try {
                        // For formulas, try to get the cached result first
                        CellType cachedType = cell.getCachedFormulaResultType();
                        if (cachedType == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                            return DATE_FORMATTER.format(cell.getLocalDateTimeCellValue());
                        }

                        // If cached result isn't a date, evaluate the formula
                        FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                        CellValue cellValue = evaluator.evaluate(cell);
                        return getValueFromCellValue(cellValue, cell);
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
     * Helper method to extract value from a CellValue object
     */
    private String getValueFromCellValue(CellValue cellValue, Cell originalCell) {
        switch (cellValue.getCellType()) {
            case STRING:
                return cellValue.getStringValue();

            case NUMERIC:
                if (DateUtil.isCellDateFormatted(originalCell)) {
                    return DATE_FORMATTER.format(originalCell.getLocalDateTimeCellValue());
                } else {
                    double value = cellValue.getNumberValue();
                    return value == Math.floor(value) ?
                            String.valueOf((int) value) :
                            String.valueOf(value);
                }

            case BOOLEAN:
                return String.valueOf(cellValue.getBooleanValue());

            default:
                return "";
        }
    }

    /**
     * Returns the list of exams imported from the Excel file
     */
    public List<Exam> getExams() {
        return exams;
    }

    /**
     * Prints all imported exams to the console in a formatted table
     */
    public void printExams() {
        if (exams.isEmpty()) {
            LOGGER.info("No exams imported.");
            return;
        }

        System.out.println("\n===== IMPORTED EXAMS =====");
        System.out.println("Total exams: " + exams.size());
        System.out.println("===========================\n");

        // Print header
        System.out.printf("%-10s | %-30s | %-15s | %-20s | %-10s | %-15s%n",
                "Course Code", "Course Name", "Exam Type", "Date from", "Date to", "Platform", "Responsible");
        System.out.println("-".repeat(120));

        // Print each exam
        for (Exam exam : exams) {
            //String dateStr = exam.getFormattedDateRange();

            System.out.printf("%-10s | %-30s | %-15s | %-20s | %-10s | %-15s%n",
                    exam.getCourseCode(),
                    truncate(exam.getCourseName(), 30),
                    truncate(exam.getExamType(), 15),
                    truncate(exam.getFormattedExamDateEnd(), 20),
                    truncate(exam.getFormattedExamDate(), 20),
                    truncate(exam.getPlatform(), 10),
                    truncate(exam.getResponsible(), 15));
        }
    }

    /**
     * Export exams to a CSV file
     */
    public boolean exportToCsv(String filePath) {
        if (exams.isEmpty()) {
            LOGGER.warning("No exams to export.");
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write header
            writer.write("CourseCode,CourseName,ExamType,Honorar,Platform,ExamDate,ExamDateEnd,Responsible,InternalSensor,InternalSensor2,ExternalSensor,Honorar2,Comment");
            writer.newLine();

            // Write data
            for (Exam exam : exams) {
                StringBuilder line = new StringBuilder();
                line.append(escapeCSV(exam.getCourseCode())).append(CSV_DELIMITER);
                line.append(escapeCSV(exam.getCourseName())).append(CSV_DELIMITER);
                line.append(escapeCSV(exam.getExamType())).append(CSV_DELIMITER);
                line.append(escapeCSV(exam.getHonorar())).append(CSV_DELIMITER);
                line.append(escapeCSV(exam.getPlatform())).append(CSV_DELIMITER);
                line.append(escapeCSV(exam.getFormattedExamDate())).append(CSV_DELIMITER);
                line.append(escapeCSV(exam.getFormattedExamDateEnd())).append(CSV_DELIMITER);
                line.append(escapeCSV(exam.getResponsible())).append(CSV_DELIMITER);
                line.append(escapeCSV(exam.getInternalSensor())).append(CSV_DELIMITER);
                line.append(escapeCSV(exam.getInternalSensor2())).append(CSV_DELIMITER);
                line.append(escapeCSV(exam.getExternalSensor())).append(CSV_DELIMITER);
                line.append(escapeCSV(exam.getHonorar2())).append(CSV_DELIMITER);
                line.append(escapeCSV(exam.getComment())).append(CSV_DELIMITER);

                writer.write(line.toString());
                writer.newLine();
            }

            LOGGER.info("Exams successfully exported to: " + filePath);
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error exporting to CSV: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Escape special characters for CSV format
     */
    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }

        // Escape quotes and wrap in quotes if contains special chars
        value = value.replace("\"", "\"\"");
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = "\"" + value + "\"";
        }

        return value;
    }

    /**
     * Truncate a string to the specified length
     */
    private String truncate(String str, int maxLength) {
        if (str == null) {
            return "";
        }
        return str.length() <= maxLength ? str : str.substring(0, maxLength - 3) + "...";
    }

    /**
     * Close the workbook to release resources
     */
    public void close() {
        try {
            if (workbook != null) {
                workbook.close();
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error closing workbook: " + e.getMessage(), e);
        }
    }

    /**
     * Static method to import exams from a file
     */
    public static List<Exam> importFromFile(String filePath) {
        Importer importer = null;
        try {
            File excelFile = new File(filePath);
            if (!excelFile.exists() || !excelFile.isFile()) {
                LOGGER.severe("Error: File does not exist or is not valid: " + filePath);
                return null;
            }

            LOGGER.info("Starting import from file: " + filePath);
            importer = new Importer(excelFile);
            importer.importExcelToObjects();
            importer.printExams();

            // Export to CSV with the same name but .csv extension
            String csvFilePath = filePath.replaceFirst("\\.[^.]+$", ".csv");
            importer.exportToCsv(csvFilePath);

            return importer.getExams();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading file: " + e.getMessage(), e);
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error: " + e.getMessage(), e);
            return null;
        } finally {
            if (importer != null) {
                importer.close();
            }
        }
    }
}
