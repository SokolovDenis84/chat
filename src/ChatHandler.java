import exceptions.BreakConnectionException;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ChatHandler extends Thread {
    private final Socket socket;
    private final List<String> badWords;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private static List<ChatHandler> handlers = Collections.synchronizedList(new ArrayList<>());
    private static final int MAX_CONNECTED = 5;

    public ChatHandler(Socket socket, List<String> badWords) throws IOException {
        this.socket = socket;
        this.badWords = badWords;
        dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    @Override
    public void run() {

        handlers.add(this);

        System.out.println("size: " + handlers.size());

        try {
//            while (true) { // todo flag
            while (handlers.size() <= MAX_CONNECTED) {
                String message = dataInputStream.readUTF();

                if (hasBadWord(message)) {
                    System.err.println("Break the connection with " + this.getName());
                    throw new BreakConnectionException();
                    //throw new BreakException();
                } else {
                    broadcast(message);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (BreakConnectionException e) {
            e.printStackTrace();
        } finally {
            handlers.remove(this);
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcast(String message) {
        synchronized (handlers) {
            Iterator<ChatHandler> iterator = handlers.iterator();
            while (iterator.hasNext()) {
                ChatHandler chatHandler = iterator.next();
                notifyEveryone(chatHandler, message);
//                try {
//                    // todo DZ отдельный метод
//                    synchronized (chatHandler.dataOutputStream) {
//                        chatHandler.dataOutputStream.writeUTF(message);
//                    }
//                    chatHandler.dataOutputStream.flush();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }

    private void notifyEveryone(ChatHandler chatHandler, String message) {
        try {
            synchronized (chatHandler.dataOutputStream) {
                chatHandler.dataOutputStream.writeUTF(message);
            }
            chatHandler.dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean hasBadWord(String string) {
        if (badWords != null) {
            for (String badWord : badWords) {
                if (string.toLowerCase().contains(badWord.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }
}