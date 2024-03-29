package reactive.view;

import reactive.controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public final class GUI extends JFrame implements ActionListener {

    private final JButton start;
    private final JButton pause;
    private final JButton resume;
    private final JButton chooseDir;
    private final JTextField selectedDir;
    private final JTextField wordTextField;
    private String selectedDirPath;
    private final JTextArea data;
    private final Controller controller;
    private int found = 0;
    private int analyzed = 0;
    private int matching = 0;

    public GUI(final Controller controller) {
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
        this.wordTextField = new JTextField();

        final Container cp = getContentPane();
        final JPanel panel = new JPanel();

        final Box p0 = new Box(BoxLayout.X_AXIS);
        p0.add(this.chooseDir);
        p0.add(this.start);
        p0.add(this.pause);
        p0.add(this.resume);
        final Box p1a = new Box(BoxLayout.X_AXIS);
        final Box p1b = new Box(BoxLayout.X_AXIS);
        p1a.add(this.selectedDir);
        p1b.add(this.wordTextField);
        final Box p1 = new Box(BoxLayout.X_AXIS);
        p1.add(this.data);
        final Box p2 = new Box(BoxLayout.Y_AXIS);
        p2.add(p0);
        p2.add(Box.createVerticalStrut(10));
        p2.add(p1b);
        p2.add(Box.createVerticalStrut(10));
        p2.add(p1a);
        p2.add(Box.createVerticalStrut(10));
        p2.add(p1);
        panel.add(p2);
        cp.add(panel);

        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent ev){
                System.exit(-1);
            }
            public void windowClosed(WindowEvent ev){
                System.exit(-1);
            }
        });
        this.chooseDir.addActionListener(this);
        this.start.addActionListener(this);
        this.pause.addActionListener(this);
        this.resume.addActionListener(this);
    }

    private void workerExecution(boolean s) {
        this.analyzed++;
        if (s)
            this.matching++;
        this.updateData();
    }

    private void masterExecution(String s) {
        this.found++;
        this.updateData();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final Object src = e.getSource();
        if (src == this.chooseDir) {
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
        } else if (src == this.start) {
            if (!this.wordTextField.getText().isEmpty()) {
                this.data.setText("");
                this.controller.notifyStarted(this.selectedDirPath, "Ricci");
                controller.getMasterStream().subscribe(this::masterExecution);
                controller.getWorkerStream().subscribe(this::workerExecution, error -> {}, this::resetState);
                this.chooseDir.setEnabled(false);
                this.resume.setEnabled(false);
                this.pause.setEnabled(true);
                this.start.setEnabled(false);
            }
        } else if (src == this.pause) {
            this.controller.notifyPaused();
            this.pause.setEnabled(false);
            this.resume.setEnabled(true);
        } else if (src == this.resume){
            this.controller.notifyResumed();
            this.pause.setEnabled(true);
            this.resume.setEnabled(false);
        }
    }

    public void updateData() {
        SwingUtilities.invokeLater(()-> {
            final String text =
                "Analyzing... \n" +
                "---------------------\n" +
                "Total PDFs: " + found + "\n" +
                "Analyzed PDFs: " + analyzed + "\n" +
                "Matching PDFs: " + matching + "\n" +
                "---------------------\n";
            this.data.setText(text);
        });
    }

    public void resetState() {
        System.out.println("CHIAMATO RESET STATE NELLA GUI");
        SwingUtilities.invokeLater(()-> {
            this.chooseDir.setEnabled(true);
            this.start.setEnabled(true);
            this.pause.setEnabled(false);
            this.resume.setEnabled(false);
            this.found = 0;
            this.analyzed = 0;
            this.matching = 0;
        });
    }

    public void display() {
        SwingUtilities.invokeLater(() -> this.setVisible(true));
    }

}