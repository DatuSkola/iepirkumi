package lv.kauguri.iepirkumi.files;

import java.io.*;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.function.Function;

public class ReadCSV {
    public static void read(String filePath, Function<String, Object> function) {
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(filePath));
            while ((line = br.readLine()) != null) {
                function.apply(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
