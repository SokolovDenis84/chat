package readers;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Чтение строк из файла
 * Denis
 * 13.05.2018
 */
public class WordFileReader implements IFileReader {
    @Override
    public List read(String file) {
        List<String> words = new ArrayList<>();
        DataInputStream dataInputStream = null;
        try {
            dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            while (dataInputStream.available() > 0) {
                String word = dataInputStream.readUTF();
                words.add(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return words;
    }
}
