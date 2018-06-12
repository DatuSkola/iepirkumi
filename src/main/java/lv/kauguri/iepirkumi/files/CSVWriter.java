package lv.kauguri.iepirkumi.files;

import lv.kauguri.iepirkumi.data.Column;
import lv.kauguri.iepirkumi.data.Data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        for (Column column : data.columns) {
            bufferedWriter.write(prefix + column);
            prefix = ", ";
        }
        bufferedWriter.write("\n");

        for (Map<Column, String> row : data.rows) {
            prefix = "";
            for (Column column : data.columns) {
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
