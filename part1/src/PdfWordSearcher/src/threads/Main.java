package threads;

import threads.controller.Controller;
import threads.model.SharedData;
import threads.view.View;

public class Main {
    public static void main(String[] args) {
        final SharedData sd =  new SharedData();
        final Controller controller = new Controller(sd);
        final View view = new View(controller);
        controller.setView(view);
        view.display();
    }
}
