package reactive.view;

import reactive.controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class GUI extends JFrame  implements ActionListener {

    private final JButton start;
    private final JButton pause;
    private final JButton resume;
    private final JButton chooseDir;
    private final JTextField selectedDir;
    private String selectedDirPath;
    private final JTextArea data;

    private final Controller controller;

    public GUI(Controller controller){
        setTitle("PDF Searcher");
        setSize(400,240);
        this.controller = controller;
        data = new JTextArea(3, 20);
        data.setEditable(false);
        data.setText("");
        selectedDir = new JTextField();
        selectedDir.setEditable(false);
        chooseDir = new JButton("Select directory");
        start = new JButton("Start");
        start.setEnabled(false);
        pause = new JButton("Pause");
        pause.setEnabled(false);
        resume = new JButton("Resume");
        resume.setEnabled(false);

        Container cp = getContentPane();
        JPanel panel = new JPanel();

        Box p0 = new Box(BoxLayout.X_AXIS);
        p0.add(chooseDir);
        p0.add(start);
        p0.add(pause);
        p0.add(resume);
        Box p1a = new Box(BoxLayout.X_AXIS);
        p1a.add(selectedDir);
        Box p1 = new Box(BoxLayout.X_AXIS);
        p1.add(data);
        Box p2 = new Box(BoxLayout.Y_AXIS);
        p2.add(p0);
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

        chooseDir.addActionListener(this);
        start.addActionListener(this);
        pause.addActionListener(this);
        resume.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == chooseDir) {
            JFileChooser fileChooser = new JFileChooser();

            fileChooser.setCurrentDirectory(new File("."));
            fileChooser.setDialogTitle("Select the directory");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);

            int code = fileChooser.showOpenDialog(this);
            if (code == JFileChooser.APPROVE_OPTION) {
                File f = fileChooser.getSelectedFile();
                selectedDirPath = f.getAbsolutePath();
                selectedDir.setText("..." + selectedDirPath.substring(selectedDirPath.length() - 20));
                start.setEnabled(true);
            }
        } else if (src == start){
            data.setText("");
            controller.resetData();
            controller.notifyStarted(selectedDirPath, "Ricci");
            chooseDir.setEnabled(false);
            resume.setEnabled(false);
            pause.setEnabled(true);
            start.setEnabled(false);
        } else if (src == pause){
            controller.notifyPaused();
            pause.setEnabled(false);
            resume.setEnabled(true);
        } else if (src == resume){
            controller.notifyResumed();
            pause.setEnabled(true);
            resume.setEnabled(false);
        }
    }

    public void updateData(int found, int analyzed, int matching) {
        SwingUtilities.invokeLater(()-> {
            String text =
                "Analyzing... \n" +
                "---------------------\n" +
                "Total PDFs: " + found + "\n" +
                "Analyzed PDFs: " + analyzed + "\n" +
                "Matching PDFs: " + matching + "\n" +
                "---------------------\n";
            data.setText(text);
        });
    }

    public void resetState() {
        SwingUtilities.invokeLater(()-> {
            chooseDir.setEnabled(true);
            start.setEnabled(true);
            pause.setEnabled(false);
            resume.setEnabled(false);
        });
    }

    public void display() {
        SwingUtilities.invokeLater(() -> {
            this.setVisible(true);
        });
    }
}