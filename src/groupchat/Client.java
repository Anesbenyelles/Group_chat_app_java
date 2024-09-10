package groupchat;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class Client {
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;
    private final Socket socket;
    private final String username;
    private Consumer<String> messageConsumer;

    public Client(Socket socket, String username) throws IOException {
        this.socket = socket;
        this.username = username;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bufferedWriter.write(username);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    public void sendMessage(String message) {
        try {
            bufferedWriter.write(username + ": " + message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything();
        }
    }

    public void sendFile(File file) throws IOException {
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
    }

    public void receiveMessages(Consumer<String> messageConsumer) {
        this.messageConsumer = messageConsumer;
        new Thread(() -> {
            String messageFromGroupChat;
            try {
                while (socket.isConnected() && (messageFromGroupChat = bufferedReader.readLine()) != null) {
                    if (messageConsumer != null) {
                        messageConsumer.accept(messageFromGroupChat);
                    }
                }
            } catch (IOException e) {
                closeEverything();
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
}
