package server;

public class User {
    private String username;
    private ClientHandler handler;

    public User(String username, ClientHandler handler) {
        this.username = username;
        this.handler = handler;
    }

    public String getUsername() { return username; }
    public ClientHandler getHandler() { return handler; }
}

