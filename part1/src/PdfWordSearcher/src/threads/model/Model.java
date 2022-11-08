package threads.model;

import threads.view.View;

public class Model {

    private final String path;
    private final String word;
    private final SharedData sd;
    private final View view;

    public Model(
        String path,
        String word,
        SharedData sd,
        View view
    ) {
        this.path = path;
        this.word = word;
        this.sd = sd;
        this.view = view;
    }

    public void start() {
        new Master(this.sd, path).start();
        for(int i = 0; i < sd.getWorkersNumber(); i++){
            new Worker(i, sd, word).start();
        }
        new Thread(() -> {
            while(!sd.areAllWorkersFinished()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                this.view.updateData(
                    sd.getFoundPdf(),
                    sd.getAnalyzedPdf(),
                    sd.getMatchingPdf()
                );
            }
        }).start();
    }
}
