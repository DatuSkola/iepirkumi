package lv.kauguri.iepirkumi;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

class CSVWriter {

    static void write(Data data) {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get("out.csv"))) {
            write(bufferedWriter, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void write(BufferedWriter bufferedWriter, Data data) throws IOException {
        String prefix = "";
        for (String column : data.columns) {
            bufferedWriter.write(prefix + column);
            prefix = ", ";
        }
        bufferedWriter.write("\n");

        for (Map<String, String> row : data.rows) {
            prefix = "";
            for (String column : data.columns) {
                String value = row.get(column);
                bufferedWriter.write(prefix);
                if (value != null) {
                    value = value.replaceAll("\n", " ");
                    value = value.replaceAll(",", " ");
                    bufferedWriter.write(value);
                }
                prefix = ", ";
            }
            bufferedWriter.write("\n");
        }
    }
}
