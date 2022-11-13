package events;

import events.controller.Controller;
import events.view.View;

public class Main {
    public static void main(String[] args) {
        final Controller controller = new Controller();
        final View view = new View(controller);
        view.display();
    }
}
