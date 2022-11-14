package events.model;

import events.controller.Data;
import events.controller.FlowController;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public final class SearcherAgent extends AbstractVerticle {

    private final FlowController fc;

    public SearcherAgent(final FlowController fc) {
        this.fc = fc;
    }

    @Override
    public void start() {
        final EventBus eb = getVertx().eventBus();
        eb.<String>consumer("queue", message -> {
            this.fc.checkPaused();
            try {
                if(message.body() != null){
                    final File file = new File(message.body());
                    final PDDocument document = PDDocument.load(file);
                    final AccessPermission ap = document.getCurrentAccessPermission();
                    if (!ap.canExtractContent())
                        throw new IOException("You do not have permission to extract text");
                    final PDFTextStripper pdfStripper = new PDFTextStripper();
                    final String text = pdfStripper.getText(document);
                    if(text.contains(Data.word))
                        eb.publish("matching", true);
                    eb.publish("analyzed", true);
                    document.close();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        vertx.undeploy(this.deploymentID());
    }

}
