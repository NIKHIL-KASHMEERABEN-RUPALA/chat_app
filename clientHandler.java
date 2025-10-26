package server;

import common.Message;
import common.LoggerUtil;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final BlockingQueue<Message> messageQueue;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String username;

    public ClientHandler(Socket socket, BlockingQueue<Message> messageQueue) {
        this.socket = socket;
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            username = (String) in.readObject();
            LoggerUtil.log(username + " connected from " + socket.getInetAddress());

            Message joinMsg = new Message("Server", username + " has joined the chat!");
            messageQueue.put(joinMsg);

            Message msg;
            while ((msg = (Message) in.readObject()) != null) {
                messageQueue.put(msg);
            }
        } catch (Exception e) {
            LoggerUtil.error("Client disconnected: " + username, e);
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    public void sendMessage(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            LoggerUtil.error("Failed to send message to " + username, e);
        }
    }
}
