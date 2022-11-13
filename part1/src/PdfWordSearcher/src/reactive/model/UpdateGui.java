package reactive.model;

import reactive.view.View;

public class UpdateGui extends Thread {

    private final SharedData sd;
    private final View view;

    public UpdateGui(SharedData sd, View view) {
        this.sd = sd;
        this.view = view;
    }

    public void run() {
        while(!sd.isAnalysisClosed()) {
            System.out.println("DENTRO");
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
