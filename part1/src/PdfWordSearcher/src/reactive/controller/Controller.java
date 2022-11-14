package reactive.controller;

import io.reactivex.rxjava3.core.Flowable;
import reactive.model.AnalyzerAgent;
import reactive.model.SharedData;
import reactive.model.UpdateGui;
import reactive.view.View;

public final class Controller {

    private SharedData sd;
    private View view;

    public Controller() {
        this.sd = new SharedData();
    }

    public void setView(final View view) {
        this.view = view;
    }

    public void notifyStarted(final String path, final String word) {
        new UpdateGui(sd, view).start();
        new AnalyzerAgent(sd, path, word).start();
    }

    public void notifyPaused() {
        this.sd.pauseSearch();
    }

    public void notifyResumed() {
        this.sd.resumeSearch();
    }

    public void resetData() {
        this.sd = new SharedData();
    }
}