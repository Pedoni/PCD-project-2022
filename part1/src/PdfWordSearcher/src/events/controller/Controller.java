package events.controller;

import events.model.*;
import events.view.View;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;

import java.util.stream.Stream;

public class Controller {

    private FlowController fc;
    private Vertx vertx;
    private EventBus eb;

    public Controller() {
        this.fc = new FlowController();
        this.vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(Runtime.getRuntime().availableProcessors()));
        this.eb = vertx.eventBus();
    }

    public EventBus getEventBus() {
        return this.eb;
    }

    public void notifyStarted(final String path, final String word) {
        Data.word = word;
        Data.path = path;
        vertx.deployVerticle(new AnalyzerAgent(fc));
        vertx.deployVerticle(new SearcherAgent(fc));
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
