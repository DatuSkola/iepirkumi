package lv.kauguri.iepirkumi;

import org.codehaus.plexus.archiver.ArchiverException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static javax.script.ScriptEngine.FILENAME;
import static lv.kauguri.iepirkumi.FileOperations.createIfNeeded;
import static lv.kauguri.iepirkumi.FileOperations.getDir;
import static lv.kauguri.iepirkumi.FileOperations.visitEachSubSubDirectory;

public class Iepirkumi {

    static String SEP = File.separator;
    static String WORK_DIR = SEP + "data" + SEP + "iepirkumi" + SEP;
    static String ARCHIVES_DIR = WORK_DIR + "arch" + SEP;
    static String XML_DIR = WORK_DIR + "xmls" + SEP;
    static String TMP_DIR = WORK_DIR + "tmp" + SEP;
    static String XLS_DIR = WORK_DIR + "xls" + SEP;
    static String FIX_BAT = "fix.bat";

    public static void main(String[] args) throws IOException, InterruptedException {
//        FileOperations.rmrf();
//        DateRange dateRange = new DateRange(2017, 12);

//        DownloaderFTP.download(dateRange);
//        recreateDirectories();
//        UnArchiver.extract();
//        LoadXMLData.fixXmlFiles();
        LoadXMLData.loadXmls();

    }

    static void recreateDirectories() throws IOException {
        File dir = new File(ARCHIVES_DIR);

        createIfNeeded(XML_DIR);
        createIfNeeded(TMP_DIR);
        createIfNeeded(XLS_DIR);

        for(File yearDir : dir.listFiles()) {
            createIfNeeded(XML_DIR + SEP + yearDir.getName());
            createIfNeeded(XLS_DIR + SEP + yearDir.getName());

            for(File monthDir : yearDir.listFiles()) {
                File xmlMonthDir = new File(XML_DIR + SEP + yearDir.getName() + SEP + monthDir.getName());
                File xlsMonthDir = new File(XLS_DIR + SEP + yearDir.getName() + SEP + monthDir.getName());
                createIfNeeded(xmlMonthDir);
                createIfNeeded(xlsMonthDir);
            }
        }
    }


}
