package com.antdev99.learning;

import com.antdev99.learning.netty.MiniHttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        logger.info("Starting Mini HTTP Server on port {}", port);

        MiniHttpServer server = new MiniHttpServer(port);
        try {
            server.start();
        } catch (IOException e) {
            logger.error("Failed to start server", e);
            throw new RuntimeException(e);
        } finally {
            logger.info("Shutting down Mini HTTP Server");
            server.shutdown();
        }
    }
}

