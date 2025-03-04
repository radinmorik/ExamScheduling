package no.hiof.examScheduling.view;


import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private ExamPanel examPanel;
    private FileDragDropPanel fileDragDropPanel;

    // Variable to store the selected Excel file path
    private File selectedExcelFile;

    public MainFrame() {
        setTitle("Smart Exam Planner");
        setSize(1200, 800);  // Larger size to fit all columns
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main panel with card layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create the file drag and drop panel
        fileDragDropPanel = new FileDragDropPanel(this::processExcelFile);

        // Create exam panel
        examPanel = new ExamPanel();

        // Add panels to the card layout
        mainPanel.add(fileDragDropPanel, "DropPanel");
        mainPanel.add(examPanel, "Exams");

        // Initially show the file drop panel
        cardLayout.show(mainPanel, "DropPanel");

        // Create menu bar
        createMenuBar();

        // Add the main panel to the frame
        add(mainPanel);

        // Set a minimum size for the window
        setMinimumSize(new Dimension(800, 600));
    }

    /**
     * Creates and configures the application menu bar
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");

        JMenuItem openItem = new JMenuItem("Open Excel File");
        openItem.addActionListener(e -> openExcelFile());

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // View menu
        JMenu viewMenu = new JMenu("View");

        JMenuItem dropPanelItem = new JMenuItem("File Drop Panel");
        dropPanelItem.addActionListener(e -> cardLayout.show(mainPanel, "DropPanel"));

        JMenuItem examsItem = new JMenuItem("Exams View");
        examsItem.addActionListener(e -> {
            if (selectedExcelFile != null) {
                cardLayout.show(mainPanel, "Exams");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please load an Excel file first",
                        "No File Loaded",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        viewMenu.add(dropPanelItem);
        viewMenu.add(examsItem);

        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);

        // Set the menu bar
        setJMenuBar(menuBar);
    }

    /**
     * Opens a file chooser dialog to select an Excel file
     */
    private void openExcelFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Excel File");

        // Set file filter to only show Excel files
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Excel Files", "xlsx", "xls"));

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            processExcelFile(selectedFile);
        }
    }

    /**
     * Processes the selected Excel file
     *
     * @param file The Excel file to process
     */
    private void processExcelFile(File file) {
        if (file != null && file.exists()) {
            selectedExcelFile = file;

            // Update the message in the drag and drop panel
            fileDragDropPanel.setMessage("File loaded: " + file.getName());

            // Load the Excel data into the exam panel
            examPanel.loadExamData(file);

            // Switch to the exam panel view
            cardLayout.show(mainPanel, "Exams");

            // Update the frame title
            setTitle("Smart Exam Planner - " + file.getName());

            // Log to console
            System.out.println("Processed file: " + file.getAbsolutePath());
        }
    }

    /**
     * Gets the currently selected Excel file
     *
     * @return The selected Excel file or null if none is selected
     */
    public File getSelectedExcelFile() {
        return selectedExcelFile;
    }
}
