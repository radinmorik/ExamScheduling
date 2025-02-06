package no.hiof.examplanner.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ExamPanel extends JPanel {
    private JTable examTable;
    private DefaultTableModel tableModel;

    public ExamPanel() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"Kurskode", "Kursnavn", "Eksamensdato", "Type"}, 0);
        examTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(examTable);

        add(scrollPane, BorderLayout.CENTER);
    }

}