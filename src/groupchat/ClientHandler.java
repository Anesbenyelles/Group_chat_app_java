package groupchat;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javafx.stage.FileChooser;

public class ClientHandler implements Runnable {
    private static final List<ClientHandler> clientHandlers = new ArrayList<>();
    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;
    private final String username;
    private DatabaseConnection connection;
    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
    
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.username = bufferedReader.readLine();
        clientHandlers.add(this);
        broadcastMessage("SERVER: " + username + " has joined the chat!");
         connection.entrelog( username);
    }

    @Override
    public void run() {
        String messageClient;
        try {
            while (socket.isConnected()) {
                messageClient = bufferedReader.readLine();
                if (messageClient.startsWith("/file")) {
                    receiveFile(messageClient); // Handle file transfer request
                } else {
                    broadcastMessage(messageClient);
                }
            }
        } catch (IOException e) {
            closeEverything();
        }
    }

    public void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.username.equals(this.username)) {
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything();
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + username + " has left the chat.");
        connection.exitelog( username);
    }

    private void sendFile(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = socket.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            System.out.println("File sent successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveFile(String fileName) {
        try {
            File file = new File("server-directory/" + fileName);
            try (FileOutputStream fos = new FileOutputStream(file);
                 InputStream is = socket.getInputStream()) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
                System.out.println("File " + fileName + " received successfully!");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
