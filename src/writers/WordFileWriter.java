package writers;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Запись строк в файл
 * Denis
 * 13.05.2018
 */
public class WordFileWriter implements IFileWriter {
    @Override
    public void write(List<String> words, String file) {

        DataOutputStream dataOutputStream = null;
        try {
            dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            for (String word : words) {
                dataOutputStream.writeUTF(word);
            }
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
