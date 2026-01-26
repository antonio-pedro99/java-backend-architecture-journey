package com.antoniopedro;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PlainOioServer {
    public void serve(int port) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port: " + serverSocket.getLocalPort());
            try {
                while (true) {
                    final Socket socket = serverSocket.accept();
                    new Thread(() -> {
                        System.out.printf("%s New client connected\n", socket.getInetAddress().getHostName());
                        OutputStream out;
                        try {
                            out = socket.getOutputStream();
                            out.write("Hi".getBytes());
                            out.flush();
                            out.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } finally {
                            try {
                                socket.close();
                            } catch (IOException e) {
                                // NO
                            }
                        }
                    }).start();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
