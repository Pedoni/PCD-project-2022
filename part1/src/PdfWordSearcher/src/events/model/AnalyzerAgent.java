package events.model;

import events.controller.Data;
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

    public AnalyzerAgent(FlowController fc) {
        this.fc = fc;
    }

    @Override
    public void start() {
        final EventBus eb = getVertx().eventBus();
        try (Stream<Path> walkStream = Files.walk(Paths.get(Data.path))) {
            walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
                fc.checkPaused();
                if (f.toString().endsWith("pdf")) {
                    eb.publish("found", true);
                    eb.publish( "queue", f.toString());
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
