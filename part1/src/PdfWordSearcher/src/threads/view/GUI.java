package threads.view;

import threads.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GUI extends JFrame {

    private final CentralPanel centralPanel;
    private static final int height = 500;
    private static final int width = 500;
    private final String path;
    private final JTextField textField;
    private final SharedData sd;

    public GUI(String path, SharedData sd){
        this.path = path;
        this.sd = sd;
        setTitle("PDF Word Searcher");
        setSize(width, height);
        setResizable(false);
        centralPanel = new CentralPanel(width, height, sd);
        centralPanel.setVisible(false);
        JButton searchWordButton = new JButton("Search word");
        this.textField = new JTextField();
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new GridLayout(1, 2));
        northPanel.add(textField);
        northPanel.add(searchWordButton);
        getContentPane().add(northPanel, BorderLayout.NORTH);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new GridLayout(1, 1));
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        southPanel.add(exitButton);
        getContentPane().add(southPanel, BorderLayout.SOUTH);

        searchWordButton.addActionListener(a -> initSearch(this.path));

        getContentPane().add(centralPanel, BorderLayout.CENTER);
        this.setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getRootPane().setDefaultButton(searchWordButton);
        setLocationRelativeTo(null);
    }

    private void initSearch(String path) {
        if(!textField.getText().isEmpty()){
            centralPanel.setVisible(true);
            new Master(this.sd, path).start();
            final int cores = Runtime.getRuntime().availableProcessors();
            for(int i  = 0; i < cores; i++){
                new Worker(i, sd,textField.getText()).start();
            }

            new Thread(() -> {
                while(true) {
                    display(sd);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public void display(SharedData sd){
        try {
            SwingUtilities.invokeAndWait(() -> {
                centralPanel.display(sd.getFoundPdf(), sd.getAnalyzedPdf(), sd.getMatchingPdf());
                repaint();
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}