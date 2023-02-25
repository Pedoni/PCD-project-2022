package tasks.model;

public final class SharedData {

    private boolean searchPaused = false;

    public synchronized boolean isSearchPaused() {
        return this.searchPaused;
    }

    public synchronized void checkPaused() {
        if(this.isSearchPaused()){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public synchronized void resumeSearch() {
        this.searchPaused = false;
        this.notifyAll();
    }

    public synchronized void pauseSearch() {
        this.searchPaused = true;
    }
}
