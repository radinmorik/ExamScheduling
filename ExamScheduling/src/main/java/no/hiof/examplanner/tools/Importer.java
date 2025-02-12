package no.hiof.examplanner.tools;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.regex.Pattern;

public class Importer {

    private Workbook workbook;
    private Sheet sheet;

    public String subjectCodePattern = "^[A-Z]{3}\\\\d{5}";

    private Importer(File excelFile) throws InvalidFormatException, IOException {
        workbook = new XSSFWorkbook(excelFile);
        sheet = workbook.getSheetAt(0);
    }

    public static void main(String[] args) throws InvalidFormatException, IOException {
        File excelFile = new File("C:\\Users\\olema\\Desktop\\excel_host24.xlsx");
        Importer reader = new Importer(excelFile);
        reader.importExcelToObjects();
    }

    private void importExcelToObjects() throws IOException {

        // find rows with exams
        for (Row row : sheet) {
            Cell firstCell = row.getCell(0);
            if (Pattern.matches(subjectCodePattern, firstCell.getStringCellValue().trim())){
                System.out.println();
                for (Cell cell : row) {
                    printCellValue(cell);
                    System.out.print("\t");
                }
            }
        }
        workbook.close();
    }

    private void readFromExcelFile() throws IOException {
        for (Row row : sheet) {
            System.out.println();
            for (Cell cell : row) {
                printCellValue(cell);
                System.out.print("\t");
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
}


