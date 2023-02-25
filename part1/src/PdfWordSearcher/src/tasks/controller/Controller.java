package tasks.controller;

import tasks.model.Model;
import tasks.view.View;

public final class Controller {

    private View view;
    private Model model;

    public void setView(final View view) {
        this.view = view;
    }

    public void notifyStarted(final String path, final String word) {
        model = new Model(path, word, this.view);
        model.start();
    }

    public void notifyPaused() {
        this.model.pauseSearch();
    }

    public void notifyResumed() {
        this.model.resumeSearch();
    }

}