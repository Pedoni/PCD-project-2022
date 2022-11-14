package threads.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public final class Master extends Thread {

    private final SharedData sd;
    private final String path;

    public Master(final SharedData sd, final String path) {
        this.sd = sd;
        this.path = path;
    }

    public void run() {
        try (final Stream<Path> walkStream = Files.walk(Paths.get(this.path))) {
            walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
                sd.checkPaused();
                if (f.toString().endsWith("pdf")) {
                    sd.incrementFoundPdf();
                    sd.addToQueue(f.toString());
                }
            });
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        System.out.println("Master finished");
        sd.stopMaster();
    }

}
