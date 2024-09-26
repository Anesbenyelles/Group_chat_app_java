package groupchat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;

public class Server {
    private final ServerSocket serverSocket;
    private Connection connection;
    // Constructor initializes the server socket
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    // Method to start the server and accept client connections
    public void startServer() {
        try {
            // Continuously listen for new client connections
            while (!serverSocket.isClosed()) {
                // Accept a new client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected!");
   
                // Create a new ClientHandler for the connected client
                ClientHandler clientHandler = new ClientHandler(clientSocket);

                // Start a new thread to handle the client's communication
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            // Close the server if an exception occurs
            closeServer();
            e.printStackTrace();
        }
    }

    // Method to close the server socket
    public void closeServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(9806)) {
            Server server = new Server(serverSocket);
            server.startServer(); // Start the server
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
