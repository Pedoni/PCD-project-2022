package reactive;

import reactive.controller.Controller;
import reactive.view.View;

public final class Main {
    public static void main(String[] args) {
        final Controller controller = new Controller();
        final View view = new View(controller);
        controller.setView(view);
        view.display();
    }
}
