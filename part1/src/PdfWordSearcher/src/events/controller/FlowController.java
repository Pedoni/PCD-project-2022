package events.controller;

public final class FlowController {

    private boolean searchPaused = false;

    public synchronized void checkPaused() {

        if(this.isSearchPaused()){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public synchronized boolean isSearchPaused() {
        return searchPaused;
    }

    public synchronized void resumeSearch() {
        this.searchPaused = false;
        this.notifyAll();
    }

    public synchronized void pauseSearch() {
        this.searchPaused = true;
    }
}
