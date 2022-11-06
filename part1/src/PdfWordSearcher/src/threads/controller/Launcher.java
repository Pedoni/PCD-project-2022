package threads.controller;

import threads.model.SharedData;
import threads.view.GUI;

import javax.swing.*;

public class Launcher {

    public static void main(String[] args) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fc.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            new GUI(fc.getSelectedFile().getPath(), new SharedData());
        } else if (result == JFileChooser.CANCEL_OPTION) {
            System.out.println("Cancel was selected");
            System.exit(0);
        }
    }
}