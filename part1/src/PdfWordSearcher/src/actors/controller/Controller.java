package actors.controller;

import actors.actors.AnalyzerActor;
import actors.actors.CounterActor;
import actors.protocols.CounterProtocol;
import actors.protocols.SearchAnalyzeProtocol;
import actors.view.View;
import actors.actors.ViewerActor;
import akka.actor.typed.ActorSystem;

public class Controller {

    private View view;
    private final FlowController flowController;

    public Controller() {
        this.flowController = new FlowController();
    }

    public void setView(View view) {
        this.view = view;
    }

    public void notifyStarted(
        String path,
        String word
    ) {
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

    }
}