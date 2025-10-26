package client;

import common.Message;
import common.LoggerUtil;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private Socket socket;
    private ObjectOutputStream out;
    private String username;

    public void startClient() {
        try {
            socket = new Socket(ClientConstants.SERVER_HOST, ClientConstants.SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter username: ");
            username = scanner.nextLine();
            out.writeObject(username);
            out.flush();

            new Thread(new MessageReceiver(socket)).start();

            while (true) {
                String msgText = scanner.nextLine();
                if (msgText.equalsIgnoreCase("/quit")) {
                    LoggerUtil.log("Disconnecting...");
                    socket.close();
                    break;
                }
                Message msg = new Message(username, msgText);
                out.writeObject(msg);
                out.flush();
            }
        } catch (IOException e) {
            LoggerUtil.error("Error connecting to server", e);
        }
    }

    public static void main(String[] args) {
        new ChatClient().startClient();
    }
}
