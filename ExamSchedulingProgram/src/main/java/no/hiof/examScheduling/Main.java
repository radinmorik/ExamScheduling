package no.hiof.examScheduling;

import no.hiof.examScheduling.view.MainFrame;

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

