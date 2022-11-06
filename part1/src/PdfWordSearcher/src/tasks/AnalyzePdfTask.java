package tasks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

public class AnalyzePdfTask implements Callable<Void> {

    private final SharedData sd;
    private final String path;

    public AnalyzePdfTask(SharedData sd, String path) {
        this.sd = sd;
        this.path = path;
    }

    @Override
    public Void call() {
        try (Stream<Path> walkStream = Files.walk(Paths.get(this.path))) {
            walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
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
        return null;
    }
}
