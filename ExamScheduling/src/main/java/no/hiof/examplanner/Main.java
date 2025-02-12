package no.hiof.examplanner;

import no.hiof.examplanner.view.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Application started");

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();

            frame.setVisible(true);
        });
    }

}
