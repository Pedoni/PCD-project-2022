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

    public ViewerActor(final ActorContext<SearchAnalyzeProtocol> context) {
        super(context);
    }

    @Override
    public Receive<SearchAnalyzeProtocol> createReceive() {
        return newReceiveBuilder()
                .onMessage(SearchAnalyzeProtocol.UpdateGuiMessage.class, this::onUpdateGuiMessage)
                .onMessage(SearchAnalyzeProtocol.ResetGuiMessage.class, this::onResetGuiMessage)
                .build();
    }

    private Behavior<SearchAnalyzeProtocol> onResetGuiMessage(final SearchAnalyzeProtocol.ResetGuiMessage message) {
        ViewerActor.view.resetState();
        return this;
    }

    private Behavior<SearchAnalyzeProtocol> onUpdateGuiMessage(final SearchAnalyzeProtocol.UpdateGuiMessage message) {
        ViewerActor.view.updateData(message.found(), message.analyzed(), message.matching());
        return this;
    }

    public static Behavior<SearchAnalyzeProtocol> create(final View view) {
        ViewerActor.view = view;
        return Behaviors.setup(ViewerActor::new);
    }
}
