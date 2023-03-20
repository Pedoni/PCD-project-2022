package org.example.puzzle.server;

import org.example.puzzle.client.GeneralClientIF;

public class User {

    public String name;
    public GeneralClientIF client;

    public User(String name, GeneralClientIF client) {
        this.name = name;
        this.client = client;
    }

    public String getName(){
        return name;
    }

    public GeneralClientIF getClient() {
        return client;
    }

}
