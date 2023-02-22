package threads.controller;

import threads.model.Master;
import threads.model.Monitor;
import threads.model.Worker;
import threads.view.View;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class Controller {

    private Monitor sd;
    private View view;
    private final int nWorkers;

    public Controller() {
        this.sd = new Monitor();
        this.nWorkers = Runtime.getRuntime().availableProcessors() + 1;
    }

    public void setView(final View view) {
        this.view = view;
    }

    public void notifyStarted(final String path, final String word) {
        final BlockingQueue<String> queue = new LinkedBlockingQueue<>(this.nWorkers);
        new Master(this.sd, path, queue).start();
        for(int i = 0; i < this.nWorkers; i++){
            new Worker(this.sd, word, queue).start();
        }
        new Thread(() -> {
            while(this.sd.getClosedWorkers() < this.nWorkers) {
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
        }).start();
    }

    public void notifyPaused() {
        this.sd.pauseSearch();
    }

    public void notifyResumed() {
        this.sd.resumeSearch();
    }

    public void resetData() {
        this.sd = new Monitor();
    }
}