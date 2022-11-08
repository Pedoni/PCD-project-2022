package threads.controller;

import threads.model.Model;
import threads.model.SharedData;
import threads.view.View;

public class Controller {

    private final SharedData sd;
    private View view;

    public Controller(SharedData sd) {
        this.sd = sd;
    }

    public synchronized void setView(View view) {
        this.view = view;
    }

    public synchronized void notifyStarted(
        String path,
        String word
    ) {
        new Model(path, word, sd, view).start();
    }

    public synchronized void notifyPaused() {
        this.sd.pauseSearch();
    }

    public synchronized void notifyResumed() {
        this.sd.resumeSearch();
    }
}