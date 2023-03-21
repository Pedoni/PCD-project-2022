package org.example.puzzle.utils;

import org.example.puzzle.client.GeneralClientIF;

import java.io.Serializable;

public class User implements Serializable {

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
