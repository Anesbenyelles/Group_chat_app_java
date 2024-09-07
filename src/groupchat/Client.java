package groupchat;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;
    private final Socket socket;
    private final String username;

    // Constructor initializes the client with the server socket and username
    public Client(Socket socket, String username) {
        this.socket = socket;
        this.username = username;
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeEverything(); // Close connection on error
            throw new RuntimeException(e);
        }
    }

    // Method to send messages to the server
    public void sendMessage() {
        try {
            // Send the username as the first message to the server
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                // Read message from the console and send to the server
                String message = scanner.nextLine();
                bufferedWriter.write(username + ": " + message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(); // Close connection on error
        }
    }

    // Method to receive messages from the server
    public void receiveMessages() {
        new Thread(() -> {
            String messageFromGroupChat;
            while (socket.isConnected()) {
                try {
                    // Read message from the server and print to the console
                    messageFromGroupChat = bufferedReader.readLine();
                    System.out.println(messageFromGroupChat);
                } catch (IOException e) {
                    closeEverything(); // Close connection on error
                    break;
                }
            }
        }).start();
    }

    // Method to close all resources (socket, reader, writer)
    public void closeEverything() {
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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username:");
        String username = scanner.nextLine();

        try (Socket socket = new Socket("localhost", 9806)) {
            Client client = new Client(socket, username);
            client.receiveMessages(); // Start receiving messages from the server
            client.sendMessage(); // Start sending messages to the server
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
