package events;

import events.controller.Controller;
import events.view.View;

public final class Main {
    public static void main(String[] args) {
        new View(new Controller()).display();
    }
}
