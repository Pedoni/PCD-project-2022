package tasks;

import tasks.controller.Controller;
import tasks.model.SharedData;
import tasks.view.View;

public class Main {
    public static void main(String[] args) {
        final SharedData sd =  new SharedData();
        final Controller controller = new Controller(sd);
        final View view = new View(controller);
        controller.setView(view);
        view.display();
    }
}
