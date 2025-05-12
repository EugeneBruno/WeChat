package server;

import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(12345);
        UserRegistry registry = new UserRegistry();

        System.out.println("Chat server started...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler handler = new ClientHandler(clientSocket, registry);
            handler.start();
        }
    }
}
