package no.hiof.examplanner.tools;

import no.hiof.examplanner.models.Exam;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import java.util.regex.Pattern;

public class Importer {

    private Workbook workbook;
    private Sheet sheet;
    private List<Exam> exams; // ArrayList to store Exam objects

    public String subjectCodePattern = "^[A-Z]{3}\\d{5}";

    public Importer(File excelFile) throws InvalidFormatException, IOException {
        workbook = new XSSFWorkbook(excelFile);
        sheet = workbook.getSheetAt(0);
        exams = new ArrayList<>();
    }

    /*
    public static void main(String[] args) throws InvalidFormatException, IOException {
        File excelFile = new File("C:\\Users\\Shvan\\Desktop\\excel_host24.xlsx");
        Importer importer = new Importer(excelFile);
        importer.readFromExcelFile();
        importer.printExams();
    }
    */

    private void importExcelToObjects() throws IOException {

        // find rows with exams
        for (Row row : sheet) {
            Cell firstCell = row.getCell(0);
            if (firstCell != null && firstCell.getCellType() == CellType.STRING) {
                String cellValue = firstCell.getStringCellValue().trim();

                if (Pattern.matches(subjectCodePattern, cellValue)) {
                    System.out.println();
                    for (Cell cell : row) {
                        printCellValue(cell);
                        System.out.print("\t");
                    }
                }
            }
        }
        workbook.close();
    }

    private void printCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                System.out.print(cell.getStringCellValue());
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    System.out.print(new SimpleDateFormat("MM-dd-yyyy").format(cell.getDateCellValue()));
                } else {
                    System.out.print((int) cell.getNumericCellValue());
                }
                break;
            default:
                break;
        }
    }

    private void readFromExcelFile() throws IOException {
        Iterator<Row> rowIterator = sheet.iterator();
        if (rowIterator.hasNext()) rowIterator.next(); // Skip header row

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Exam exam = createExamFromRow(row);
            if (exam != null) {
                exams.add(exam);
            }
        }
        workbook.close();
    }

    private Exam createExamFromRow(Row row) {
        try {
            String courseCode = getCellValue(row.getCell(0));
            String courseName = getCellValue(row.getCell(1));
            String examType = getCellValue(row.getCell(2));
            String honorar = getCellValue(row.getCell(3));
            String platform = getCellValue(row.getCell(4));
            LocalDateTime examDate = getDateFromCell(row.getCell(5)); // Handle LocalDateTime
            String responsible = getCellValue(row.getCell(6));
            String internalSensor = getCellValue(row.getCell(7));
            String internalSensor2 = getCellValue(row.getCell(8));
            String externalSensor = getCellValue(row.getCell(9));
            String honorar2 = getCellValue(row.getCell(10));
            String comment = getCellValue(row.getCell(11));

            return new Exam(courseCode, courseName, examType, honorar, platform, examDate,
                    responsible, internalSensor, internalSensor2, externalSensor, honorar2, comment);

        } catch (Exception e) {
            System.err.println("Error processing row: " + e.getMessage());
            return null;
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new SimpleDateFormat("MM-dd-yyyy").format(cell.getDateCellValue());
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    private LocalDateTime getDateFromCell(Cell cell) {
        if (cell != null && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        return null;
    }

    private void printExams() {
        for (Exam exam : exams) {
            System.out.println(exam);
        }
    }

    public static void importFromFile(String filePath) {
        try {
            File excelFile = new File(filePath);

            if (!excelFile.exists() || !excelFile.isFile()) {
                System.out.println("Feil: Filen finnes ikke eller er ikke en gyldig fil!");
                return;
            }

            Importer reader = new Importer(excelFile);
            reader.importExcelToObjects();
        } catch (InvalidFormatException | IOException e) {
            System.out.println("Feil ved lesing av fil: " + e.getMessage());
        }
    }
}
