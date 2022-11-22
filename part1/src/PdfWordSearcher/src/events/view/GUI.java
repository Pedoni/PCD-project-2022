package events.view;

import events.controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public final class GUI extends JFrame  implements ActionListener {

    private final JButton start;
    private final JButton pause;
    private final JButton resume;
    private final JButton chooseDir;
    private final JTextField selectedDir;
    private String selectedDirPath;
    private final JTextArea data;
    private final Controller controller;
    private int found = 0;
    private int analyzed = 0;
    private int matching = 0;
    private boolean isMasterRunning = true;

    public GUI(Controller controller){
        setTitle("PDF Searcher");
        setSize(400,240);
        this.controller = controller;
        this.data = new JTextArea(3, 20);
        this.data.setEditable(false);
        this.data.setText("");
        this.selectedDir = new JTextField();
        this.selectedDir.setEditable(false);
        this.chooseDir = new JButton("Select directory");
        this.start = new JButton("Start");
        this.start.setEnabled(false);
        this.pause = new JButton("Pause");
        this.pause.setEnabled(false);
        this.resume = new JButton("Resume");
        this.resume.setEnabled(false);

        final Container cp = getContentPane();
        final JPanel panel = new JPanel();

        final Box p0 = new Box(BoxLayout.X_AXIS);
        p0.add(chooseDir);
        p0.add(start);
        p0.add(pause);
        p0.add(resume);
        final Box p1a = new Box(BoxLayout.X_AXIS);
        p1a.add(selectedDir);
        final Box p1 = new Box(BoxLayout.X_AXIS);
        p1.add(data);
        final Box p2 = new Box(BoxLayout.Y_AXIS);
        p2.add(p0);
        p2.add(Box.createVerticalStrut(10));
        p2.add(p1a);
        p2.add(Box.createVerticalStrut(10));
        p2.add(p1);
        panel.add(p2);
        cp.add(panel);

        addWindowListener(new WindowAdapter(){
            public void windowClosing(final WindowEvent ev){
                System.exit(-1);
            }
            public void windowClosed(final WindowEvent ev){
                System.exit(-1);
            }
        });

        this.controller.getEventBus().consumer("masterfinished", message -> {
            this.isMasterRunning = false;
            if (this.found == this.analyzed) {
                this.resetState();
            }
        });

        this.controller.getEventBus().consumer("found", message -> {
            this.found += 1;
            this.updateData();
        });

        this.controller.getEventBus().consumer("analyzed", message -> {
            this.analyzed += 1;
            this.updateData();
            System.out.println("ANALYZED: " + analyzed + ", FOUND: " + found + ", MASTER RUN: " + isMasterRunning);
            if ((this.found == this.analyzed) && !this.isMasterRunning) {
                this.resetState();
            }
        });

        this.controller.getEventBus().consumer("matching", message -> {
            this.matching += 1;
            this.updateData();
        });
        this.chooseDir.addActionListener(this);
        this.start.addActionListener(this);
        this.pause.addActionListener(this);
        this.resume.addActionListener(this);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final Object source = e.getSource();
        if (source == this.chooseDir) {
            final JFileChooser fileChooser = new JFileChooser();

            fileChooser.setCurrentDirectory(new File("."));
            fileChooser.setDialogTitle("Select the directory");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);

            final int code = fileChooser.showOpenDialog(this);
            if (code == JFileChooser.APPROVE_OPTION) {
                final File f = fileChooser.getSelectedFile();
                this.selectedDirPath = f.getAbsolutePath();
                this.selectedDir.setText("..." + this.selectedDirPath.substring(this.selectedDirPath.length() - 20));
                this.start.setEnabled(true);
            }
        } else if (source == this.start){
            this.data.setText("");
            this.controller.notifyStarted(this.selectedDirPath, "Ricci");
            this.chooseDir.setEnabled(false);
            this.resume.setEnabled(false);
            this.pause.setEnabled(true);
            this.start.setEnabled(false);
        } else if (source == this.pause){
            this.controller.notifyPaused();
            this.pause.setEnabled(false);
            this.resume.setEnabled(true);
        } else if (source == this.resume){
            this.controller.notifyResumed();
            this.pause.setEnabled(true);
            this.resume.setEnabled(false);
        }
    }

    private void updateData() {
        SwingUtilities.invokeLater(()-> {
            final String text =
                "Analyzing... \n" +
                "---------------------\n" +
                "Total PDFs: " + this.found + "\n" +
                "Analyzed PDFs: " + this.analyzed + "\n" +
                "Matching PDFs: " + this.matching + "\n" +
                "---------------------\n";
            this.data.setText(text);
        });
    }

    private void resetState() {
        System.out.println("CHIAMATO RESET DA GUI");
        SwingUtilities.invokeLater(()-> {
            this.chooseDir.setEnabled(true);
            this.start.setEnabled(true);
            this.pause.setEnabled(false);
            this.resume.setEnabled(false);
            this.isMasterRunning = true;
            this.found = 0;
            this.analyzed = 0;
            this.matching = 0;
        });
    }

    public void display() {
        SwingUtilities.invokeLater(() -> this.setVisible(true));
    }
}