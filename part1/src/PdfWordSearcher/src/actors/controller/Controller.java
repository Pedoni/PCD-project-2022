package actors.controller;

import actors.model.UpdateGui;
import actors.view.View;

public class Controller {

    private View view;

    public void setView(View view) {
        this.view = view;
    }

    public void notifyStarted(
        String path,
        String word
    ) {
        new UpdateGui(view).start();

    }

    public void notifyPaused() {
        //this.sd.pauseSearch();
    }

    public void notifyResumed() {
        //this.sd.resumeSearch();
    }

    public void resetData() {
        //this.sd = new SharedData();
    }
}