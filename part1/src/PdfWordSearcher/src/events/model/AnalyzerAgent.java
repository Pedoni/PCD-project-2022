package events.model;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class AnalyzerAgent extends AbstractVerticle {

    private final SharedData sd;
    private final String path;

    public AnalyzerAgent(SharedData sd, String path) {
        this.sd = sd;
        this.path = path;
    }

    @Override
    public void start() {
        EventBus eb = getVertx().eventBus();
        try (Stream<Path> walkStream = Files.walk(Paths.get(this.path))) {
            walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
                sd.checkPaused();
                if (f.toString().endsWith("pdf")) {
                    sd.incrementFoundPdf();
                    eb.publish( "queue", f.toString());
                }
            });
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        System.out.println("Master finished");
        sd.stopMaster();
        try {
            vertx.undeploy(this.deploymentID());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
