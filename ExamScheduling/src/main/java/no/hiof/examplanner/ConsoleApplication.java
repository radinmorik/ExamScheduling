package no.hiof.examplanner;



import no.hiof.examplanner.tools.Importer;
import java.util.Scanner;

public class ConsoleApplication {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Skriv inn filstien til Excel-filen:");
        String filePath = scanner.nextLine();

        Importer.importFromFile(filePath);

        scanner.close();
    }
}
