package events.view;

import events.controller.Controller;

public class View {
    private final GUI gui;

    public View(final Controller controller){
        gui = new GUI(controller);
    }

    public void display() {
        gui.display();
    }
}
