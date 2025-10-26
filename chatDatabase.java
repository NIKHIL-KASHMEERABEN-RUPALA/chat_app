package server;

import common.LoggerUtil;
import common.Message;
import java.sql.*;

public class ChatDatabase {
    private static final String DB_URL = "jdbc:sqlite:chat_history.db";

    public ChatDatabase() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS messages (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    sender TEXT,
                    content TEXT,
                    timestamp TEXT
                )
            """);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to initialize database", e);
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public void saveMessage(Message message) {
        String sql = "INSERT INTO messages(sender, content, timestamp) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, message.getSender());
            pstmt.setString(2, message.getContent());
            pstmt.setString(3, message.getTimestamp().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LoggerUtil.error("Failed to save message", e);
        }
    }

    public void printChatHistory() {
        String sql = "SELECT * FROM messages";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            LoggerUtil.log("---- Chat History ----");
            while (rs.next()) {
                System.out.println("[" + rs.getString("timestamp") + "] "
                        + rs.getString("sender") + ": " + rs.getString("content"));
            }
        } catch (SQLException e) {
            LoggerUtil.error("Failed to fetch history", e);
        }
    }
}
