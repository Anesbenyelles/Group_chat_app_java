package groupchat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnection {
    private Connection connection;

    public DatabaseConnection() {
        String url = "jdbc:mysql://127.0.0.1:3306/logging_system";
        String username = "root";
        String password = "MysqlING";
        try {
            this.connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void addClient(String username) {
        String query = "INSERT INTO users (username, created_at) VALUES (?, CURRENT_TIMESTAMP)";
        try (PreparedStatement stmt = this.connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
            System.out.println("Client added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void entrelog(String username) {
    	
    	String query = "INSERT INTO logs (username, action,timestamp) VALUES (?, ?,CURRENT_TIMESTAMP)";
        try (PreparedStatement stmt = this.connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, "loged in");
            stmt.executeUpdate();
            System.out.println("logs added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
 public void exitelog(String username) {
    	
    	String query = "INSERT INTO logs (username, action,timestamp) VALUES (?, ?,CURRENT_TIMESTAMP)";
        try (PreparedStatement stmt = this.connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, "loged out");
            stmt.executeUpdate();
            System.out.println("logs added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void editClient(String oldUsername, String newUsername) {
        String query = "UPDATE users SET username = ? WHERE username = ?";
        try (PreparedStatement stmt = this.connection.prepareStatement(query)) {
            stmt.setString(1, newUsername);
            stmt.setString(2, oldUsername);
            stmt.executeUpdate();
            System.out.println("Client edited successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isAdmin(String username) {
        String query = "SELECT role FROM users WHERE username = ?";
        try (PreparedStatement stmt = this.connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return "admin".equalsIgnoreCase(rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
