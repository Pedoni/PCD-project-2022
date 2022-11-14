package tasks.model;

import tasks.view.View;

public final class UpdateGui extends Thread {

    private final SharedData sd;
    private final View view;

    public UpdateGui(final SharedData sd, final View view) {
        this.sd = sd;
        this.view = view;
    }

    public void run() {
        while(!this.sd.isAnalysisClosed()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.view.updateData(
                this.sd.getFoundPdf(),
                this.sd.getAnalyzedPdf(),
                this.sd.getMatchingPdf()
            );
        }
        this.view.resetState();
    }
}
