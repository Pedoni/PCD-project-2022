package tasks.controller;

import tasks.model.Model;
import tasks.model.SharedData;
import tasks.view.View;

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
        new Model(path, word, this.sd, this.view).start();
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