package events.controller;

import events.model.AnalyzerAgent;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;

import java.util.stream.Stream;

public final class Controller {

    private final Vertx vertx;

    public Controller() {
        this.vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(Runtime.getRuntime().availableProcessors()));
    }

    public EventBus getEventBus() {
        return this.vertx.eventBus();
    }

    public void notifyStarted(final String path, final String word) {
        this.vertx.deployVerticle(new AnalyzerAgent(path, word)).andThen(
            t -> Stream.iterate(0, n -> n + 1)
                    .limit(Runtime.getRuntime().availableProcessors())
                    .forEach(x -> getEventBus().send("next", true))
        );

    }

    public void notifyPaused() {
        getEventBus().send("pause", true);
    }

    public void notifyResumed() {
        getEventBus().send("resume", true);
        Stream.iterate(0, n -> n + 1)
                .limit(Runtime.getRuntime().availableProcessors())
                .forEach(x -> getEventBus().send("next", true));
    }

}
