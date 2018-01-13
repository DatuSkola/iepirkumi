package lv.kauguri.iepirkumi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static lv.kauguri.iepirkumi.Iepirkumi.SEP;
import static lv.kauguri.iepirkumi.Iepirkumi.WORK_DIR;

class FileOperations {

    static void rmrf() throws IOException, InterruptedException {
        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec("rm -rf " + WORK_DIR);
        pr.waitFor();
    }

    static void createIfNeeded(String dirPath) throws IOException {
        createIfNeeded(new File(dirPath));
    }

    static void createIfNeeded(File dir) throws IOException {
        if(!dir.exists()) {
            dir.mkdir();
        }
    }

    static void forceDelete(File file) {
        if(file.isDirectory()) {
            for(File child : file.listFiles()) {
                forceDelete(child);
            }
            file.delete();

        } else {
            file.delete();
        }
    }

    static String readFile(File file) {
        try {
            String content = readFile(file.getCanonicalPath());
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    static String readFile(String path) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)));
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void writeFile(Path path, String contents) {
        try {
            Files.write(path, contents.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void visitEachSubSubDirectory(String dir, MyConsumer<String, String> myConsumer) {
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

    static String getDir(String dir, String year, String month) {
        return dir + SEP + year + SEP + month;
    }


}
