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
    private ActorSystem<SearchAnalyzeProtocol> analyzer;
    private ActorSystem<CounterProtocol> counter;

    public void setView(final View view) {
        this.view = view;
    }

    public void notifyStarted(final String path, final String word) {
        Data.path = path;
        Data.word = word;

        analyzer = ActorSystem.create(AnalyzerActor.create(), "analyzer");

        final ActorSystem<SearchAnalyzeProtocol> viewer =
                ActorSystem.create(ViewerActor.create(view), "viewer");

        counter = ActorSystem.create(CounterActor.create(viewer, null), "counter");

        analyzer.tell(new SearchAnalyzeProtocol.BootMessage(analyzer, counter));
    }

    public void notifyPaused() {
        analyzer.tell(new SearchAnalyzeProtocol.PauseMessage());
    }

    public void notifyResumed() {
        //this.flowController.resumeSearch();
        analyzer.tell(new SearchAnalyzeProtocol.ResumeMessage());
        analyzer.tell(new SearchAnalyzeProtocol.StepMessage(analyzer, counter));
    }

}