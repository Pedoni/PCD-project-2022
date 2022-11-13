package events.model;

import events.controller.FlowController;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class AnalyzerAgent extends AbstractVerticle {

    private final FlowController fc;
    private final String path;

    public AnalyzerAgent(FlowController fc, String path) {
        this.fc = fc;
        this.path = path;
    }

    @Override
    public void start() {
        EventBus eb = getVertx().eventBus();
        try (Stream<Path> walkStream = Files.walk(Paths.get(this.path))) {
            walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
                fc.checkPaused();
                if (f.toString().endsWith("pdf")) {
                    eb.publish("found", true);
                    eb.send( "queue", f.toString());
                }
            });
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        eb.publish("masterfinished", true);
        try {
            vertx.undeploy(this.deploymentID());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
