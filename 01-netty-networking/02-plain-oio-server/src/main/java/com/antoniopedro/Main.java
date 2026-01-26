package com.antoniopedro;

import java.io.IOException;

public class Main {
    static int port = 8080;
    public static void main(String[] args) {
        PlainOioServer server = new PlainOioServer();
        try {
            server.serve(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}