# Group Chat Application

This is a simple group chat application built using Java socket programming. The project allows multiple clients to connect to a server and communicate with each other in real-time. Each client runs on a separate thread, managed by the server.

## Prerequisites

- Java Development Kit (JDK) 22
- A terminal or command prompt


## How to Run

### 1. Compile the Project

1. Open a terminal or command prompt.
2. Navigate to the project directory:

    ```bash
    cd C:\Users\dell\javaproject\groupchat
    ```

3. Compile the Java files using the JDK 22 compiler:

    ```bash
    javac -d bin src/groupchat/Server.java src/groupchat/ClientHandler.java src/groupchat/Client.java
    ```

    This command will compile the `.java` files and place the `.class` files in the `bin` directory.

### 2. Run the Server

1. Start the server by running the `Server` class:

    ```bash
    java -cp bin groupchat.Server
    ```

    The server will start and listen for client connections on port `9806`.

### 3. Run the Clients

1. Open another terminal or command prompt window.
2. Navigate to the project directory:

    ```bash
    cd C:\Users\dell\javaproject\groupchat
    ```

3. Run the `Client` class:

    ```bash
    java -cp bin groupchat.Client
    ```

4. When prompted, enter your username. The client will connect to the server, and you will be able to send and receive messages.

5. Repeat steps 1-4 in additional terminal windows to connect more clients to the server.

### 4. Testing Multiple Clients

- Open multiple terminal windows and run the `Client` class in each one to simulate multiple users in the chat. Each client should be able to send and receive messages from other connected clients.

### 5. Closing the Server

- To stop the server, you can close the terminal running the `Server` class, or press `Ctrl + C` in the terminal.

## Notes

- **JDK 22 Compatibility**: The project has been tested with JDK 22. Make sure your `JAVA_HOME` environment variable is set to the path of JDK 22.
- **Port Configuration**: The server listens on port `9806` by default. If this port is already in use, you can change it by modifying the `ServerSocket` instantiation in the `Server.java` file.

## Future Improvements

- Implement user authentication.
- Add a graphical user interface (GUI) for the chat application.
- Implement private messaging between clients.
- Add support for more complex commands (e.g., `/list` to show connected users).

## License

This project is licensed under the MIT License. See the `LICENSE` file for more details.

