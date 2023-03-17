package threads;

import threads.controller.Controller;
import threads.view.View;

import java.time.LocalDateTime;

public final class Main {
    public static void main(String[] args) {
        final Controller controller = new Controller();
        final View view = new View(controller);
        controller.setView(view);
        view.display();
    }
}
