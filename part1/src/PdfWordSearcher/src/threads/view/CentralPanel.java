package threads.view;

import threads.model.SharedData;

import javax.swing.*;
import java.awt.*;

public class CentralPanel extends JPanel {

    private final JLabel totalLabel;
    private final JLabel analyzedLabel;
    private final JLabel foundLabel;

    public CentralPanel(int w, int h, SharedData sd) {
        this.setLayout(new GridLayout(3, 3));
        setSize(w,h);

        this.add(getLabel("Total PDFs"));
        this.add(getLabel("Analyzed PDFs"));
        this.add(getLabel("Matching PDFs"));

        totalLabel = new JLabel("0");
        this.totalLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        this.totalLabel.setVerticalAlignment(SwingConstants.TOP);
        this.totalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(totalLabel);

        analyzedLabel = new JLabel("0");
        this.analyzedLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        this.analyzedLabel.setVerticalAlignment(SwingConstants.TOP);
        this.analyzedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(analyzedLabel);

        foundLabel = new JLabel("0");
        this.foundLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        this.foundLabel.setVerticalAlignment(SwingConstants.TOP);
        this.foundLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(foundLabel);

        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(a -> {

        });
        this.add(stopButton);

        JButton pauseButton = new JButton("Pause");
        pauseButton.addActionListener(a -> sd.pauseSearch());
        this.add(pauseButton);

        JButton resumeButton = new JButton("Resume");
        resumeButton.addActionListener(a -> {
            sd.resumeSearch();
        });
        this.add(resumeButton);
    }

    private JLabel getLabel(final String text) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    public void display(int total, int analyzed, int found){
        this.totalLabel.setText(Integer.toString(total));
        this.analyzedLabel.setText(Integer.toString(analyzed));
        this.foundLabel.setText(Integer.toString(found));
        repaint();
    }

}