package reactive.view;

import reactive.controller.Controller;

public final class View {
    private final GUI gui;

    public View(final Controller controller){
        this.gui = new GUI(controller);
    }

    public void updateData(final int found, final int analyzed, final int matching) {
        this.gui.updateData(found, analyzed, matching);
    }

    public void resetState() {
        this.gui.resetState();
    }

    public void display() {
        this.gui.display();
    }
}
