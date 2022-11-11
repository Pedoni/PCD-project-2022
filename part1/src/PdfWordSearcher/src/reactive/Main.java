package reactive;

import reactive.controller.Controller;
import reactive.model.SharedData;
import reactive.view.View;

public class Main {
    public static void main(String[] args) {
        final SharedData sd =  new SharedData();
        final Controller controller = new Controller(sd);
        final View view = new View(controller);
        controller.setView(view);
        view.display();
    }
}
