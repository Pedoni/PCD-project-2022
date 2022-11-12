package events.controller;

import events.model.*;
import events.view.View;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import events.model.UpdateGui;

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

        int WORKER_POOL_SIZE = 8;

        DeploymentOptions masterOpts = new DeploymentOptions()
                .setWorkerPoolSize(WORKER_POOL_SIZE);

        DeploymentOptions workerOpts = new DeploymentOptions()
                .setWorker(true)
                .setInstances(WORKER_POOL_SIZE)
                .setWorkerPoolSize(WORKER_POOL_SIZE);

        vertx.deployVerticle(new AnalyzerAgent(sd, path));
        vertx.deployVerticle(new SearcherAgent(sd, word));
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