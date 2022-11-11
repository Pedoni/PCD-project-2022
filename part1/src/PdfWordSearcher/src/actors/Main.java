package actors;

import actors.controller.Controller;
import actors.view.View;

public class Main {
    public static void main(String[] args) {
        final Controller controller = new Controller();
        final View view = new View(controller);
        controller.setView(view);
        view.display();
    }
}
