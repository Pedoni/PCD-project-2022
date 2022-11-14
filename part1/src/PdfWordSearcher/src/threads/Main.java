package threads;

import threads.controller.Controller;
import threads.model.SharedData;
import threads.view.View;

public final class Main {
    public static void main(String[] args) {
        final Controller controller = new Controller();
        final View view = new View(controller);
        controller.setView(view);
        view.display();
    }
}
