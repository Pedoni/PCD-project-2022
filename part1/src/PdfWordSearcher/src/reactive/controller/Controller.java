package reactive.controller;

import io.vertx.core.Vertx;
import reactive.model.AnalyzerAgent;
import reactive.model.SharedData;
import reactive.model.UpdateGui;
import reactive.view.View;

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
        new UpdateGui(sd, view).start();
        final Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new AnalyzerAgent(sd, path, word));
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