package no.hiof.examScheduling.view;


import no.hiof.examScheduling.model.Exam;
import no.hiof.examScheduling.service.ExcelService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Panel to display exam data in a table with improved date handling
 */
public class ExamPanel extends JPanel {
    private JTable examTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private ExcelService excelService;
    private List<Exam> exams;  // Store loaded exams

    /**
     * Constructor that creates the exam panel
     */
    public ExamPanel() {
        setLayout(new BorderLayout());
        excelService = new ExcelService();

        // Create table with columns matching the Excel format
        String[] columns = {
                "Emnekode", "Emnenavn", "Vurderingsform", "Honorar",
                "Platform", "Dato fra", "Dato til", "Ansvarlig",
                "Intern sensor", "Intern sensor 2", "Ekstern sensor", "Honorar 2", "Kommentar"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        examTable = new JTable(tableModel);

        // Configure table appearance
        examTable.setFillsViewportHeight(true);
        examTable.setRowHeight(25);
        examTable.getTableHeader().setReorderingAllowed(false);

        // Create a sorter for the table
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        examTable.setRowSorter(sorter);

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(examTable);

        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");

        searchButton.addActionListener(e -> {
            String searchText = searchField.getText().toLowerCase();
            if (searchText.trim().isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
            }
        });

        searchField.addActionListener(e -> searchButton.doClick());

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Add filtering by course code
        JLabel filterLabel = new JLabel("Filter by Course:");
        JComboBox<String> courseFilterComboBox = new JComboBox<>();
        courseFilterComboBox.addItem("All Courses");

        courseFilterComboBox.addActionListener(e -> {
            String selected = (String) courseFilterComboBox.getSelectedItem();
            if ("All Courses".equals(selected)) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("^" + selected, 0)); // Filter by first column (course code)
            }
        });

        searchPanel.add(filterLabel);
        searchPanel.add(courseFilterComboBox);

        // Add components to the panel
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add status panel at the bottom
        JPanel statusPanel = new JPanel(new BorderLayout());
        JLabel statusLabel = new JLabel("Ready");
        statusPanel.add(statusLabel, BorderLayout.WEST);

        // Add export button
        JButton exportButton = new JButton("Export Data");
        exportButton.addActionListener(e -> exportData());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(exportButton);
        statusPanel.add(buttonPanel, BorderLayout.EAST);

        add(statusPanel, BorderLayout.SOUTH);
    }

    /**
     * Loads exam data from an Excel file
     *
     * @param file The Excel file to load
     */
    public void loadExamData(File file) {
        if (file == null || !file.exists()) {
            JOptionPane.showMessageDialog(this,
                    "Invalid Excel file",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Clear existing data
        tableModel.setRowCount(0);

        // Read the data
        exams = excelService.readExamData(file);

        // Populate the course code filter (for the ComboBox)
        updateCourseCodeFilter(exams);

        // Add data to the table
        for (Exam exam : exams) {
            addExamToTable(exam);
        }

        // Update the status
        if (exams.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No exam data found in the file",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            System.out.println("Loaded " + exams.size() + " exams from file: " + file.getName());
            // Print a few exam objects to the console for debugging
            printExamsToConsole(exams, 5);
        }
    }

    /**
     * Adds an exam to the table model with updated date handling
     */
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
                "",                             // 10: Ekstern sensor
                exam.getHonorar2(),             // 11: Honorar 2
                exam.getComment()             // 12: Kommentar

        });

        //tableModel.addRow(rowData);
    }

    /**
     * Updates the course code filter ComboBox with available course codes
     */
    private void updateCourseCodeFilter(List<Exam> exams) {
        // Get the ComboBox
        JComboBox<String> courseFilterComboBox = null;
        JPanel searchPanel = (JPanel) getComponent(0);

        for (Component comp : searchPanel.getComponents()) {
            if (comp instanceof JComboBox) {
                courseFilterComboBox = (JComboBox<String>) comp;
                break;
            }
        }

        if (courseFilterComboBox != null) {
            courseFilterComboBox.removeAllItems();
            courseFilterComboBox.addItem("All Courses");

            // Add unique course codes
            exams.stream()
                    .map(Exam::getCourseCode)
                    .distinct()
                    .sorted()
                    .forEach(courseFilterComboBox::addItem);
        }
    }

    /**
     * Exports the data to a new Excel file (placeholder)
     */
    private void exportData() {
        JOptionPane.showMessageDialog(this,
                "Export functionality would be implemented here.",
                "Export Data",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Prints some exams to the console for debugging
     */
    private void printExamsToConsole(List<Exam> exams, int limit) {
        System.out.println("\n==== Sample Exam Data ====");
        System.out.println(String.format("%-10s | %-30s | %-15s | %-20s | %-10s",
                "Code", "Name", "Type", "Date Range", "Platform"));
        System.out.println("-".repeat(90));

        int count = 0;
        for (Exam exam : exams) {
            if (count++ >= limit) break;

            System.out.println(String.format("%-10s | %-30s | %-15s | %-20s | %-10s",
                    exam.getCourseCode(),
                    truncate(exam.getCourseName(), 30),
                    truncate(exam.getExamType(), 15),
                    exam.getFormattedExamDateEnd() + "-" + exam.getFormattedExamDate(),
                    exam.getPlatform()));
        }
        System.out.println("==== End of Sample ====\n");
    }

    /**
     * Truncates a string to the specified length
     */
    private String truncate(String str, int length) {
        if (str == null) return "";
        return str.length() <= length ? str : str.substring(0, length - 3) + "...";
    }
}
