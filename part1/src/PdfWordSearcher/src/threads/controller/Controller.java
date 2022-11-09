package threads.controller;

import threads.model.Model;
import threads.model.SharedData;
import threads.view.View;

public class Controller {

    private SharedData sd;
    private View view;

    public Controller(SharedData sd) {
        this.sd = sd;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void notifyStarted(
        String path,
        String word
    ) {
        new Model(path, word, sd, view).start();
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