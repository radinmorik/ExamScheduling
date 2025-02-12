package no.hiof.examplanner.tools;

import no.hiof.examplanner.models.Exam;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Importer {

    private Workbook workbook;
    private Sheet sheet;
    private List<Exam> exams; // ArrayList to store Exam objects

    public Importer(File excelFile) throws InvalidFormatException, IOException {
        workbook = WorkbookFactory.create(excelFile);
        sheet = workbook.getSheetAt(0);
        exams = new ArrayList<>();
    }

    public static void main(String[] args) throws InvalidFormatException, IOException {
        File excelFile = new File("C:\\Users\\Shvan\\Desktop\\excel_host24.xlsx");
        Importer importer = new Importer(excelFile);
        importer.readFromExcelFile();
        importer.printExams();
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
            List<LocalDateTime> examDates = Exam.parseExamDates(getCellValue(row.getCell(5))); // Handle multiple dates
            String responsible = getCellValue(row.getCell(6));
            String internalSensor = getCellValue(row.getCell(7));
            String internalSensor2 = getCellValue(row.getCell(8));
            String externalSensor = getCellValue(row.getCell(9));
            String honorar2 = getCellValue(row.getCell(10));
            String comment = getCellValue(row.getCell(11));

            return new Exam(courseCode, courseName, examType, honorar, platform, examDates,
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
                    return new SimpleDateFormat("dd.MM.yyyy HH:mm").format(cell.getDateCellValue());
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    private void printExams() {
        for (Exam exam : exams) {
            System.out.println(exam);
        }
    }
}
