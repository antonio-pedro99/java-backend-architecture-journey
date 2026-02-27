package com.antdev99.learning;

import com.antdev99.learning.netty.MiniHttpServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        MiniHttpServer server = new MiniHttpServer(port);
        try {
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            server.shutdown();
        }
    }
}