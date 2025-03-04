package no.hiof.examScheduling.service;


import no.hiof.examScheduling.excel.ExcelProcessor;
import no.hiof.examScheduling.model.Exam;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class to handle Excel file operations, focused on UI interaction.
 * Uses the shared ExcelProcessor for parsing and validation.
 */
public class ExcelService {
    private static final Logger LOGGER = Logger.getLogger(ExcelService.class.getName());

    /**
     * Reads exam data from an Excel file for display in the UI
     *
     * @param file The Excel file to read
     * @return A list of Exam objects
     */
    public List<Exam> readExamData(File file) {
        List<Exam> exams = new ArrayList<>();

        if (file == null || !file.exists()) {
            LOGGER.warning("Invalid file provided");
            return exams;
        }

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = createWorkbook(file, fis)) {

            // Get the first sheet
            Sheet sheet = workbook.getSheetAt(0);

            // Find the header row
            int headerRowIndex = findHeaderRow(sheet);
            if (headerRowIndex < 0) {
                LOGGER.warning("Could not identify header row in Excel file");
                return exams;
            }

            // Process data rows
            for (int i = headerRowIndex + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell firstCell = row.getCell(ExcelProcessor.COL_COURSE_CODE);
                String firstCellValue = ExcelProcessor.getCellValueAsString(firstCell).trim();

                if (firstCellValue.isEmpty() ||
                        firstCellValue.startsWith("Mastersemester") ||
                        !ExcelProcessor.isValidCourseCode(firstCellValue)) {
                    continue;
                }

                Exam exam = ExcelProcessor.createExamFromRow(row);
                if (exam != null) {
                    exams.add(exam);
                    LOGGER.fine("Added exam: " + exam.getCourseCode() + " - " + exam.getCourseName());
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading Excel file", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error processing file", e);
        }

        LOGGER.info("Read " + exams.size() + " exams from file");
        return exams;
    }

    /**
     * Finds the index of the header row in the sheet
     */
    private int findHeaderRow(Sheet sheet) {
        // Look in first 20 rows for header
        for (int i = 0; i < 20 && i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Cell firstCell = row.getCell(0);
            if (firstCell != null && firstCell.getCellType() == CellType.STRING) {
                String value = firstCell.getStringCellValue().toLowerCase().trim();
                if (value.contains("emnekode")) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Creates the appropriate workbook based on file extension
     */
    private Workbook createWorkbook(File file, FileInputStream fis) throws IOException {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".xlsx")) {
            return new XSSFWorkbook(fis);
        } else if (fileName.endsWith(".xls")) {
            return new HSSFWorkbook(fis);
        } else {
            throw new IOException("Unsupported file format: " + fileName);
        }
    }

    /**
     * Performs diagnostic analysis on an Excel file, showing detailed structure.
     * Useful for debugging problematic files.
     */
    public void analyzeExcelFile(File excelFile) {
        if (excelFile == null || !excelFile.exists()) {
            LOGGER.warning("Invalid file provided for analysis");
            return;
        }

        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            System.out.println("====== EXCEL FILE ANALYSIS ======");
            System.out.println("File: " + excelFile.getName());
            System.out.println("Total rows: " + (sheet.getLastRowNum() + 1));

            // Find header row
            int headerRow = findHeaderRow(sheet);

            if (headerRow >= 0) {
                System.out.println("Header found at row: " + (headerRow + 1));
                Row headerRowObj = sheet.getRow(headerRow);

                System.out.println("\n----- HEADER COLUMNS -----");
                for (int i = 0; i < 15; i++) {
                    Cell cell = headerRowObj.getCell(i);
                    System.out.println("Column " + i + ": " + ExcelProcessor.getCellValueAsString(cell));
                }

                // Show sample data rows
                System.out.println("\n----- SAMPLE DATA ROWS -----");
                for (int rowIdx = headerRow + 1; rowIdx < headerRow + 4 && rowIdx <= sheet.getLastRowNum(); rowIdx++) {
                    Row dataRow = sheet.getRow(rowIdx);
                    if (dataRow != null) {
                        System.out.println("\nData Row " + (rowIdx + 1) + ":");
                        for (int i = 0; i < 15; i++) {
                            Cell cell = dataRow.getCell(i);
                            System.out.printf("  Col %-2d: %-30s (Type: %s)%n",
                                    i,
                                    ExcelProcessor.truncate(ExcelProcessor.getCellValueAsString(cell), 30),
                                    (cell != null ? cell.getCellType() : "NULL"));
                        }
                    }
                }
            } else {
                System.out.println("No header row found in the first 20 rows");
            }

            System.out.println("==============================");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error analyzing Excel file: " + e.getMessage(), e);
        }
    }
}