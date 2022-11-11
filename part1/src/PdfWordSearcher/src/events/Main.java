package events;

import events.controller.Controller;
import events.model.SharedData;
import events.view.View;

public class Main {
    public static void main(String[] args) {
        final SharedData sd =  new SharedData();
        final Controller controller = new Controller(sd);
        final View view = new View(controller);
        controller.setView(view);
        view.display();
    }
}
