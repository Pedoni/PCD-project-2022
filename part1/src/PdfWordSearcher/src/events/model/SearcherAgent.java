package events.model;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class SearcherAgent extends AbstractVerticle {

    private final SharedData sd;
    private final String word;

    public SearcherAgent(SharedData sd, String word) {
        this.sd = sd;
        this.word = word;
    }

    @Override
    public void start() {
        EventBus eb = getVertx().eventBus();
        eb.<String>consumer("queue", message -> {
            System.out.println("Worker: I have received the message: " + message.body());
            try {
                searchWordInPdf(this.sd, message.body());
                if(!sd.isMasterRunning()){
                    vertx.undeploy(this.deploymentID());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void searchWordInPdf(SharedData sd, String currentPath) throws IOException {
        if(currentPath != null){
            File file = new File(currentPath);
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
    }

}
