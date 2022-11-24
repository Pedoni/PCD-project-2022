package reactive.controller;

import io.reactivex.rxjava3.core.Flowable;
import reactive.model.AnalyzerAgent;
import reactive.view.View;

public final class Controller {

    private View view;
    private AnalyzerAgent analyzer;

    public Controller() {

    }

    public void setView(final View view) {
        this.view = view;
    }

    public Flowable<String> getMasterStream() {
        return analyzer.getMasterStream();
    }

    public Flowable<Boolean> getWorkerStream() {
        return analyzer.getWorkerStream();
    }

    public void notifyStarted(final String path, final String word) {
        this.analyzer = new AnalyzerAgent(path, word);
    }

    public void notifyPaused() {
        this.analyzer.pause();
    }

    public void notifyResumed() {
        this.analyzer.resume();
    }

}