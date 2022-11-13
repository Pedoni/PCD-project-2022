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
    private final Vertx vertx;
    private final EventBus eb;

    public Controller() {
        this.fc = new FlowController();
        this.vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(Runtime.getRuntime().availableProcessors()));
        this.eb = vertx.eventBus();
    }

    public EventBus getEventBus() {
        return this.eb;
    }

    public void notifyStarted(String path, String word) {
        vertx.deployVerticle(new AnalyzerAgent(fc, path));
        vertx.deployVerticle(new SearcherAgent(fc, word));
        //vertx.deployVerticle(new CounterAgent());
        //vertx.deployVerticle(new ViewAgent(view));
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