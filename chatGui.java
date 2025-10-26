package client;

import common.Message;
import common.LoggerUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class ChatGUI extends Application {
    private TextArea chatArea;
    private TextField inputField;
    private ObjectOutputStream out;
    private Socket socket;
    private String username;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Java Chat Client");

        chatArea = new TextArea();
        chatArea.setEditable(false);

        inputField = new TextField();
        inputField.setPromptText("Type a message...");
        Button sendButton = new Button("Send");

        sendButton.setOnAction(e -> sendMessage());
        inputField.setOnAction(e -> sendMessage());

        VBox layout = new VBox(10, chatArea, new HBox(10, inputField, sendButton));
        layout.setPadding(new Insets(10));

        stage.setScene(new Scene(layout, 500, 400));
        stage.show();

        connectToServer();
    }

    private void connectToServer() {
        try {
            socket = new Socket(ClientConstants.SERVER_HOST, ClientConstants.SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            username = System.getProperty("user.name");
            out.writeObject(username);
            out.flush();

            Thread receiverThread = new Thread(() -> {
                try (var in = new java.io.ObjectInputStream(socket.getInputStream())) {
                    Message msg;
                    while ((msg = (Message) in.readObject()) != null) {
                        String text = msg.toString();
                        Platform.runLater(() -> chatArea.appendText(text + "\n"));
                    }
                } catch (Exception e) {
                    LoggerUtil.error("Disconnected from server", e);
                }
            });
            receiverThread.setDaemon(true);
            receiverThread.start();

        } catch (Exception e) {
            LoggerUtil.error("Connection failed", e);
        }
    }

    private void sendMessage() {
        try {
            String text = inputField.getText();
            if (text.isEmpty()) return;
            Message msg = new Message(username, text);
            out.writeObject(msg);
            out.flush();
            inputField.clear();
        } catch (Exception e) {
            LoggerUtil.error("Failed to send message", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
