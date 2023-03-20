package tasks;

import tasks.controller.Controller;
import tasks.view.View;

public final class Main {
    public static void main(String[] args) {
        final Controller controller = new Controller();
        final View view = new View(controller);
        controller.setView(view);
        view.display();
    }
}
