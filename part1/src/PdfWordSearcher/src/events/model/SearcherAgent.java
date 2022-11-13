package events.model;

import events.controller.FlowController;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class SearcherAgent extends AbstractVerticle {

    private final FlowController fc;
    private final String word;

    public SearcherAgent(FlowController fc, String word) {
        this.fc = fc;
        this.word = word;
    }

    @Override
    public void start() {
        EventBus eb = getVertx().eventBus();
        eb.<String>consumer("queue", message -> {
            fc.checkPaused();
            try {
                if(message.body() != null){
                    File file = new File(message.body());
                    PDDocument document = PDDocument.load(file);
                    AccessPermission ap = document.getCurrentAccessPermission();
                    if (!ap.canExtractContent())
                        throw new IOException("You do not have permission to extract text");
                    PDFTextStripper pdfStripper = new PDFTextStripper();
                    String text = pdfStripper.getText(document);
                    if(text.contains(this.word)) {
                        eb.publish("matching", true);
                    }
                    eb.publish("analyzed", true);
                    document.close();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        vertx.undeploy(this.deploymentID());
        //if(!sd.isMasterRunning()){
        //    vertx.undeploy(this.deploymentID());
        //}
    }

}
