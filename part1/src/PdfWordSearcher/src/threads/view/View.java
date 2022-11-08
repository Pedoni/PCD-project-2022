package threads.view;

import threads.controller.Controller;

public class View {
    private GUI gui;

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