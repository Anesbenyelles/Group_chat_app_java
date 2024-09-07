package groupchat;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;
    private final Socket socket;
    private final String username;

    public Client(Socket socket, String username) {
        this.socket = socket;
        this.username = username;
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeEverything();
            throw new RuntimeException(e);
        }
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String message = scanner.nextLine();
                bufferedWriter.write(username + ": " + message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything();
        }
    }

    public void receiveMessages() {
        new Thread(() -> {
            String messageFromGroupChat;
            while (socket.isConnected()) {
                try {
                    messageFromGroupChat = bufferedReader.readLine();
                    System.out.println(messageFromGroupChat);
                } catch (IOException e) {
                    closeEverything();
                    break;
                }
            }
        }).start();
    }

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
            client.receiveMessages();
            client.sendMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
