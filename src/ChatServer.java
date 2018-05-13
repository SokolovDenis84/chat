import readers.WordFileReader;
import writers.WordFileWriter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class ChatServer {

    public static void main(String[] args) {

        String badWordsFile = "badWords.bin";

        // Запись в файл запрещенных слов
//        List<String> badWordsExample = Arrays.asList("бред", "мудак", "долбоёб", "гнида", "мозгоёб");
//        WordFileWriter stringFileWriter = new WordFileWriter();
//        stringFileWriter.write(badWordsExample, badWordsFile);

        // Чтение из файла запрещенных слов
        WordFileReader fileReader = new WordFileReader();
        List<String> badWords = fileReader.read(badWordsFile);

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8088);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Accepted from: " + socket.getInetAddress());
                ChatHandler chatHandler = new ChatHandler(socket, badWords);
                chatHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}