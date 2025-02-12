package no.hiof.examplanner.tools;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Importer {

    private Workbook workbook;
    private Sheet sheet;

    private Importer(File excelFile) throws InvalidFormatException, IOException {
        workbook = new XSSFWorkbook(excelFile);
        sheet = workbook.getSheetAt(0);
    }

    /* TO BE DELETED. Bruker main metode i ConsoleApplication isteden.
    public static void main(String[] args) throws InvalidFormatException, IOException {
        File excelFile = new File("/Users/radin/Documents/excel_høst24.xlsx");
        no.hiof.examplanner.tools.Importer reader = new no.hiof.examplanner.tools.Importer(excelFile);
        reader.readFromExcelFile();
    }
     */

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

    public static void importFromFile(String filePath) {
        try {
            File excelFile = new File(filePath);

            if (!excelFile.exists() || !excelFile.isFile()) {
                System.out.println("Feil: Filen finnes ikke eller er ikke en gyldig fil!");
                return;
            }

            Importer reader = new Importer(excelFile);
            reader.readFromExcelFile();
        } catch (InvalidFormatException | IOException e) {
            System.out.println("Feil ved lesing av fil: " + e.getMessage());
        }
    }
}


