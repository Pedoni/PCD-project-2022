package events.controller;

import events.model.AnalyzerAgent;
import events.model.SearcherAgent;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;

public final class Controller {

    private FlowController fc;
    private final Vertx vertx;

    public Controller() {
        this.fc = new FlowController();
        this.vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(Runtime.getRuntime().availableProcessors()));
    }

    public EventBus getEventBus() {
        return this.vertx.eventBus();
    }

    public void notifyStarted(final String path, final String word) {
        Data.word = word;
        Data.path = path;
        final DeploymentOptions workerOptions = new DeploymentOptions()
                .setWorkerPoolSize(8)
                .setWorker(true);
        this.vertx.deployVerticle(new AnalyzerAgent(this.fc));
        this.vertx.deployVerticle(new SearcherAgent(this.fc), workerOptions);
    }

    public void notifyPaused() {
        this.fc.pauseSearch();
    }

    public void notifyResumed() {
        this.fc.resumeSearch();
    }

    public void resetData() {
        this.fc = new FlowController();
    }
}
