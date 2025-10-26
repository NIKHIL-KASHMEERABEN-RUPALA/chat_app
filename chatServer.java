package server;

import common.Message;
import common.LoggerUtil;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.*;

public class ChatServer {
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(ServerConstants.SERVER_PORT)) {
            LoggerUtil.log("Server started on port " + ServerConstants.SERVER_PORT);

            // Thread to broadcast messages
            pool.submit(() -> {
                while (true) {
                    Message msg = messageQueue.take();
                    broadcast(msg);
                }
            });

            // Accept clients
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket, messageQueue);
                clients.add(handler);
                pool.submit(handler);
            }
        } catch (IOException | InterruptedException e) {
            LoggerUtil.error("Server error", e);
        }
    }

    private void broadcast(Message message) {
        LoggerUtil.log(message.toString());
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public static void main(String[] args) {
        new ChatServer().startServer();
    }
}
