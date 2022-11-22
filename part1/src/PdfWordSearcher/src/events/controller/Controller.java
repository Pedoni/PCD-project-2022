package events.controller;

import events.model.AnalyzerAgent;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

public final class Controller {

    private final Vertx vertx;

    public Controller() {
        this.vertx = Vertx.vertx();
    }

    public EventBus getEventBus() {
        return this.vertx.eventBus();
    }

    public void notifyStarted(final String path, final String word) {
        this.vertx.deployVerticle(new AnalyzerAgent(path, word)).andThen(
            t -> getEventBus().send("next", true)
        );

    }

    public void notifyPaused() {
        getEventBus().send("pause", true);
    }

    public void notifyResumed() {
        getEventBus().send("resume", true);
        getEventBus().send("next", true);
    }

}
