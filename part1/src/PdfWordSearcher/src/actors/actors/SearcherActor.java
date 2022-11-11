package actors.actors;

import actors.controller.Data;
import actors.protocols.CounterProtocol;
import actors.protocols.SearchAnalyzeProtocol;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class SearcherActor extends AbstractBehavior<SearchAnalyzeProtocol> {

    public SearcherActor(ActorContext<SearchAnalyzeProtocol> context) {
        super(context);
    }

    @Override
    public Receive<SearchAnalyzeProtocol> createReceive() {
        return newReceiveBuilder()
                .onMessage(SearchAnalyzeProtocol.SearchMessage.class, this::onSearchMessage)
                .build();
    }

    public static Behavior<SearchAnalyzeProtocol> create() {
        return Behaviors.setup(SearcherActor::new);
    }

    private Behavior<SearchAnalyzeProtocol> onSearchMessage(SearchAnalyzeProtocol.SearchMessage message) {
        try {
            if(message.currentPath() != null){
                File file = new File(message.currentPath());
                PDDocument document = PDDocument.load(file);
                PDFTextStripper pdfStripper = new PDFTextStripper();
                String text = pdfStripper.getText(document);
                if(text.contains(Data.word)) {
                    message.counter().tell(new CounterProtocol.IncrementMatchingMessage());
                }
                message.counter().tell(new CounterProtocol.IncrementAnalyzedMessage());
                document.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Behaviors.stopped();
    }

}
