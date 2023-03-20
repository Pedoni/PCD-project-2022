package reactive.view;

import reactive.controller.Controller;

public final class View {
    private final GUI gui;

    public View(final Controller controller){
        this.gui = new GUI(controller);
    }

    public void display() {
        this.gui.display();
    }

}
