package server;

import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(12345);
        UserRegistry registry = new UserRegistry();  // Ensure this constructor is public

        System.out.println("Chat server started...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler handler = new ClientHandler(clientSocket, registry);  // Ensure this constructor is public
            handler.start();
        }
    }
}

