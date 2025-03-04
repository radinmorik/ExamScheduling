package no.hiof.examScheduling;


import no.hiof.examScheduling.tools.Importer;

import java.util.Scanner;

public class ConsoleApplication {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        System.out.println("Skriv inn filstien til Excel-filen:");
        //"C:\Users\Bruker\Desktop\excel_host24_copy.xlsx"
        String filePath = "C:\\Users\\Shvan\\Desktop\\excel_host24_copy.xlsx";


        //String filePath = scanner.nextLine();

        Importer.importFromFile(filePath);

        scanner.close();
    }
}
