package groupchat;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class Chatapp extends Application {
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private Socket socket;
    private String username;
    private TextArea chatArea;
    private TextField messageField;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chat Application");

        // Main layout
        BorderPane root = new BorderPane();

        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);

        // Input area for typing messages
        messageField = new TextField();
        messageField.setPromptText("Type your message here...");

        Button sendButton = new Button("Send");
        Button sendFileButton = new Button("Send File");
        Button sendPhotoButton = new Button("Send Photo");

        // Layout for the input and buttons
        HBox inputLayout = new HBox(10, messageField, sendButton, sendFileButton, sendPhotoButton);
        inputLayout.setPadding(new Insets(10));

        // Add components to the main layout
        root.setCenter(chatArea);
        root.setBottom(inputLayout);

        // Set up the scene and show the stage
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize the client and socket connection
        initializeClient();

        // Add action listeners
        sendButton.setOnAction(e -> sendMessage());
        sendFileButton.setOnAction(e -> sendFile());
        sendPhotoButton.setOnAction(e -> sendPhoto());
    }

    private void initializeClient() {
        try {
            socket = new Socket("localhost", 9806); // Connect to the server
            username = "User"; // Replace with user input for username
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            receiveMessages(this::updateChatArea); // Update chat area with received messages
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            try {
                bufferedWriter.write(username + ": " + message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                messageField.clear();
            } catch (IOException e) {
                closeEverything();
            }
        }
    }

    private void sendFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                bufferedWriter.write("SEND_FILE");
                bufferedWriter.newLine();
                bufferedWriter.write(file.getName());
                bufferedWriter.newLine();
                bufferedWriter.write(String.valueOf(file.length()));
                bufferedWriter.newLine();
                bufferedWriter.flush();

                try (FileInputStream fis = new FileInputStream(file);
                     OutputStream os = socket.getOutputStream()) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }

                chatArea.appendText("You sent file: " + file.getName() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendPhoto() {
        // Implementation for sending a photo can go here
        System.out.println("Photo sending not implemented yet.");
    }

    private void receiveMessages(Consumer<String> messageConsumer) {
        new Thread(() -> {
            String messageFromGroupChat;
            try {
                while (socket.isConnected() && (messageFromGroupChat = bufferedReader.readLine()) != null) {
                    messageConsumer.accept(messageFromGroupChat);
                }
            } catch (IOException e) {
                closeEverything();
            }
        }).start();
    }

    private void updateChatArea(String message) {
        chatArea.appendText(message + "\n");
    }

    private void closeEverything() {
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
        launch(args);
    }
}
