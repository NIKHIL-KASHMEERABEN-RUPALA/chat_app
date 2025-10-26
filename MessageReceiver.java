package client;

import common.Message;
import common.LoggerUtil;
import java.io.ObjectInputStream;
import java.net.Socket;

public class MessageReceiver implements Runnable {
    private final Socket socket;

    public MessageReceiver(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            Message msg;
            while ((msg = (Message) in.readObject()) != null) {
                System.out.println(msg);
            }
        } catch (Exception e) {
            LoggerUtil.error("Disconnected from server", e);
        }
    }
}
