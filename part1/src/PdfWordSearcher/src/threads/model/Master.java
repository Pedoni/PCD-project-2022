package threads.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

public final class Master extends Thread {

    private final SharedData sd;
    private final String path;
    private final BlockingQueue<String> queue;

    public Master(final SharedData sd, final String path, final BlockingQueue<String> queue) {
        this.sd = sd;
        this.path = path;
        this.queue = queue;
    }

    public void run() {
        try (final Stream<Path> walkStream = Files.walk(Paths.get(this.path))) {
            walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
                this.sd.checkPaused();
                if (f.toString().endsWith("pdf")) {
                    this.sd.incrementFoundPdf();
                    try {
                        this.queue.put(f.toString());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        this.sd.stopMaster();
    }

}
