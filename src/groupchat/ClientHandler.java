package groupchat;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    // List to keep track of all connected clients
    private static final List<ClientHandler> clientHandlers = new ArrayList<>();
    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;
    private final String username;

    // Constructor initializes the client handler with the connected socket
    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        // The first message from the client is considered as the username
        this.username = bufferedReader.readLine();
        
        // Add the new client handler to the list of connected clients
        clientHandlers.add(this);
        
        // Broadcast that a new user has joined the chat
        broadcastMessage("SERVER: " + username + " has joined the chat!");
    }

    @Override
    public void run() {
        String messageClient;

        // Continuously listen for messages from the client
        try {
            while (socket.isConnected()) {
                messageClient = bufferedReader.readLine(); // Read the message from the client
                
                // Broadcast the message to all connected clients
                broadcastMessage(messageClient);
            }
        } catch (IOException e) {
            // Close the connection if an error occurs
            closeEverything();
        }
    }

    // Method to broadcast a message to all clients except the sender
    public void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.username.equals(this.username)) { // Avoid sending the message to the sender
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(); // Close connection on error
            }
        }
    }

    // Method to remove the client handler from the list and notify others
    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + username + " has left the chat.");
    }

    // Method to close all resources (socket, reader, writer)
    public void closeEverything() {
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
