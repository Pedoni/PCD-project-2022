package actors.model;

import actors.view.View;

public class UpdateGui extends Thread {

    private View view;

    public UpdateGui(View view) {
        this.view = view;
    }

    public void run() {
        /*
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
        */
    }
}
