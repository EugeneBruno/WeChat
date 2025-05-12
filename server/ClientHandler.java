package server;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket socket;
    private UserRegistry registry;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public ClientHandler(Socket socket, UserRegistry registry) {
        this.socket = socket;
        this.registry = registry;
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("Enter username:");
            username = in.readLine();

            if (!registry.registerUser(username, this)) {
                out.println("Username taken. Disconnecting.");
                socket.close();
                return;
            }

            out.println("Welcome, " + username + "!");

            String input;
            while ((input = in.readLine()) != null) {
                if (input.startsWith("SEND")) {
                    String[] parts = input.split(" ", 3);
                    String recipient = parts[1];
                    String message = parts[2];

                    User target = registry.getUser(recipient);
                    if (target != null) {
                        target.getHandler().sendMessage(username + ": " + message);
                    } else {
                        out.println("User not found.");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            registry.removeUser(username);
        }
    }
}

