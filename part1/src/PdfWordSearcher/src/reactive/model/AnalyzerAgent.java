package reactive.model;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.flowables.ConnectableFlowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.vertx.core.AbstractVerticle;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class AnalyzerAgent extends AbstractVerticle {

    private final SharedData sd;
    private final String path;
    private final String word;

    public AnalyzerAgent(SharedData sd, String path, String word) {
        this.sd = sd;
        this.path = path;
        this.word = word;
    }

    @Override
    public void start() {
        Flowable<String> source = this.genHotStream();
        source
                .onBackpressureDrop(v -> System.out.println("DROPPING: " + v))
                .observeOn(Schedulers.computation())
                .subscribe(this::searchInPdf, error -> System.out.println("ERROR: " + error));
        sd.stopMaster();
        try {
            vertx.undeploy(this.deploymentID());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Flowable<String> genHotStream() {
        Flowable<String> source = Flowable.create(emitter -> new Thread(() -> {
            try (Stream<Path> walkStream = Files.walk(Paths.get(this.path))) {
                walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
                    sd.checkPaused();
                    if (f.toString().endsWith("pdf")) {
                        this.sd.incrementFoundPdf();
                        emitter.onNext(f.toString());
                    }
                });
            } catch (IOException e){
                throw new RuntimeException(e);
            }
        }).start(), BackpressureStrategy.LATEST);
        ConnectableFlowable<String> hotObservable = source.publish();
        hotObservable.connect();
        return hotObservable;
    }

    private void searchInPdf(String v) {
        try {
            sd.checkPaused();
            if(v != null){
                File file = new File(v);
                PDDocument document = PDDocument.load(file);
                PDFTextStripper pdfStripper = new PDFTextStripper();
                String text = pdfStripper.getText(document);
                if(text.contains(this.word)) {
                    sd.incrementOccurrences();
                    System.out.println("TOTAL OCCURRENCES: " + sd.getMatchingPdf());
                }
                sd.incrementAnalyzedPdf();
                System.out.println("ANALYZED PDFs: " + sd.getAnalyzedPdf());
                document.close();
            }
            if(!sd.isMasterRunning()){
                vertx.undeploy(this.deploymentID());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
