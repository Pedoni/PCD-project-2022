package reactive.view;

import reactive.controller.Controller;

public class View {
    private final GUI gui;

    public View(Controller controller){
        gui = new GUI(controller);
    }

    public void updateData(int found, int analyzed, int matching) {
        gui.updateData(found, analyzed, matching);
    }

    public void resetState() {
        gui.resetState();
    }

    public void display() {
        gui.display();
    }
}