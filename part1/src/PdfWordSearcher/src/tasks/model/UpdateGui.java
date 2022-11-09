package tasks.model;

import tasks.view.View;

public class UpdateGui extends Thread {

    private SharedData sd;
    private View view;

    public UpdateGui(SharedData sd, View view) {
        this.sd = sd;
        this.view = view;
    }

    public void run() {
        while(!sd.isAnalysisClosed()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.view.updateData(
                    sd.getFoundPdf(),
                    sd.getAnalyzedPdf(),
                    sd.getMatchingPdf()
            );
        }
        this.view.resetState();
    }
}
