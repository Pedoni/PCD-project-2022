package actors.controller;

import actors.actors.AnalyzerActor;
import actors.actors.CounterActor;
import actors.protocols.CounterProtocol;
import actors.protocols.SearchAnalyzeProtocol;
import actors.view.View;
import actors.actors.ViewerActor;
import akka.actor.typed.ActorSystem;

public final class Controller {

    private View view;
    private FlowController flowController;

    public Controller() {
        this.flowController = new FlowController();
    }

    public void setView(final View view) {
        this.view = view;
    }

    public void notifyStarted(final String path, final String word) {
        Data.path = path;
        Data.word = word;

        final ActorSystem<SearchAnalyzeProtocol> analyzer =
                ActorSystem.create(AnalyzerActor.create(flowController), "analyzer");

        final ActorSystem<SearchAnalyzeProtocol> viewer =
                ActorSystem.create(ViewerActor.create(view), "viewer");

        final ActorSystem<CounterProtocol> counter =
                ActorSystem.create(CounterActor.create(viewer, null), "counter");

        analyzer.tell(new SearchAnalyzeProtocol.BootMessage(counter));
    }

    public void notifyPaused() {
        this.flowController.pauseSearch();
    }

    public void notifyResumed() {
        this.flowController.resumeSearch();
    }

    public void resetData() {
        this.flowController = new FlowController();
    }
}