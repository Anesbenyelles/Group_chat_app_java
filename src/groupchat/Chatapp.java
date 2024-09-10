package groupchat;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class Chatapp extends Application {

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Chat Application");

		// Main layout
		BorderPane root = new BorderPane();


		TextArea chatArea = new TextArea();
		chatArea.setEditable(false);
		chatArea.setWrapText(true);

		// Input area for typing messages
		TextField messageField = new TextField();
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

		// Add action listeners (placeholder for actual functionality)
		sendButton.setOnAction(e -> sendMessage(chatArea, messageField));
		sendFileButton.setOnAction(e -> sendFile());
		sendPhotoButton.setOnAction(e -> sendPhoto());
	}

	// Placeholder method to send a message
	private void sendMessage(TextArea chatArea, TextField messageField) {
		String message = messageField.getText();
		if (!message.isEmpty()) {
			chatArea.appendText("You: " + message + "\n");
			messageField.clear();
			// TODO: Send message to the server
		}
	}

	// Placeholder method to handle file sending
	private void sendFile() {
		// TODO: Implement file sending functionality
		System.out.println("File sending not implemented yet.");
	}

	// Placeholder method to handle photo sending
	private void sendPhoto() {
		// TODO: Implement photo sending functionality
		System.out.println("Photo sending not implemented yet.");
	}

	public static void main(String[] args) {
		launch(args);
	}
}
