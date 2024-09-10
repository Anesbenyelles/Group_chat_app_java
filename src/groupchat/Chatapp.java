package groupchat;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class Chatapp extends Application {
    private Client client;
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

        // Initialize client
        initializeClient();

        // Add action listeners
        sendButton.setOnAction(e -> sendMessage());
        sendFileButton.setOnAction(e -> sendFile());
        sendPhotoButton.setOnAction(e -> sendPhoto());
    }

    private void initializeClient() {
        try {
            Socket socket = new Socket("localhost", 9806); // Connect to the server
            client = new Client(socket, "User"); // Replace "User" with a username prompt or input
            client.receiveMessages(this::updateChatArea); // Update chat area with received messages
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty() && client != null) {
            client.sendMessage(message);
            messageField.clear();
        }
    }

    private void sendFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null && client != null) {
            try {
                client.sendFile(file); // Implement this method in Client class
                chatArea.appendText("You sent file: " + file.getName() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendPhoto() {
        // Similar implementation as sendFile, but may involve image-specific handling
        System.out.println("Photo sending not implemented yet.");
    }

    private void updateChatArea(String message) {
        chatArea.appendText(message + "\n");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
