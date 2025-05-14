package server;

import java.util.concurrent.ConcurrentHashMap;

public class UserRegistry {
    private ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    // Make sure the constructor is public
    public UserRegistry() {
    }

    public boolean registerUser(String username, ClientHandler handler) {
        return users.putIfAbsent(username, new User(username, handler)) == null;
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public boolean isUserOnline(String username) {
        return users.containsKey(username);
    }

    public void removeUser(String username) {
        users.remove(username);
    }
}
