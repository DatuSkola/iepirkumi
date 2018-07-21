package lv.kauguri.iepirkumi.files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static lv.kauguri.iepirkumi.Iepirkumi.SEP;
import static lv.kauguri.iepirkumi.Iepirkumi.WORK_DIR;

public class FileOperations {

    public static void createIfNeeded(String dirPath) throws IOException {
        createIfNeeded(new File(dirPath));
    }

    public static void createIfNeeded(File dir) throws IOException {
        if(!dir.exists()) {
            dir.mkdir();
        }
    }

    public static void forceDelete(File file) {
        if(file.isDirectory()) {
            for(File child : file.listFiles()) {
                forceDelete(child);
            }
            file.delete();

        } else {
            file.delete();
        }
    }

    public static String readFile(File file) {
        try {
            String content = readFile(file.getCanonicalPath());
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String readFile(String path) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)));
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeFile(Path path, String contents) {
        try {
            Files.write(path, contents.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void visitEachSubSubDirectory(String dir, MyConsumer<String, String> myConsumer) {
        File xmlDir = new File(dir);
        for(File yearDir : xmlDir.listFiles()) {
            for (File monthDir : yearDir.listFiles()) {
                try {
                    myConsumer.accept(yearDir.getName(), monthDir.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Stream<Dir> visitEachSubSubDirectory2(String dir) {
        List<Dir> dirList = new ArrayList<>();
        File xmlDir = new File(dir);
        for(File yearDir : xmlDir.listFiles()) {
            for (File monthDir : yearDir.listFiles()) {
                dirList.add(new Dir(yearDir.getName(), monthDir.getName()));
            }
        }
        return dirList.stream();
    }

    public static String getDir(String dir, String year, String month) {
        return dir + SEP + year + SEP + month;
    }

    public static void run(String command) {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec(command);
            InputStream is = process.getInputStream();
            int i = 0;
            while( (i = is.read() ) != -1) {
                System.out.print((char)i);
            }
            process.waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }


}
