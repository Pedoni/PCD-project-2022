package actors.actors;

import actors.protocols.SearchAnalyzeProtocol;
import actors.view.View;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class ViewerActor extends AbstractBehavior<SearchAnalyzeProtocol> {

    private static View view;

    public ViewerActor(ActorContext<SearchAnalyzeProtocol> context) {
        super(context);
    }

    @Override
    public Receive<SearchAnalyzeProtocol> createReceive() {
        return newReceiveBuilder()
                .onMessage(SearchAnalyzeProtocol.UpdateGuiMessage.class, this::onUpdateGuiMessage)
                .build();
    }

    private Behavior<SearchAnalyzeProtocol> onUpdateGuiMessage(SearchAnalyzeProtocol.UpdateGuiMessage message) {
        ViewerActor.view.updateData(message.found(), message.analyzed(), message.matching());
        return this;
    }

    public static Behavior<SearchAnalyzeProtocol> create(View view) {
        ViewerActor.view = view;
        return Behaviors.setup(ViewerActor::new);
    }
}
