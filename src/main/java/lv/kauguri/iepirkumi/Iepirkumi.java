package lv.kauguri.iepirkumi;

import lv.kauguri.iepirkumi.data.Column;
import lv.kauguri.iepirkumi.data.DateRange;
import lv.kauguri.iepirkumi.files.FileOperations;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static lv.kauguri.iepirkumi.files.FileOperations.createIfNeeded;

public class Iepirkumi {

    public static String SEP = File.separator;
    public static String WORK_DIR = SEP + "data" + SEP + "iepirkumi" + SEP;
    public static String ARCHIVES_DIR = WORK_DIR + "arch" + SEP;
    public static String XML_DIR = WORK_DIR + "xmls" + SEP;
    public static String TMP_DIR = WORK_DIR + "tmp" + SEP;
    public static String XLS_DIR = WORK_DIR + "xls" + SEP;
    public static String FIX_BAT = "fix.bat";

    public static void main(String[] args) {
//        loadData();

        XMLtoDoc.loadXmls(data -> XLSWriter.write(data, XLS_DIR + SEP + data.year + "_" + data.month + ".xlsx"));
    }

    static void loadData() throws IOException, InterruptedException {
        FileOperations.rmrf();
        DateRange dateRange = new DateRange(2017, 12);

        DownloadArchives.download(dateRange);
        recreateDirectories();
        UnpacArchivesToXml.extract();
        XMLtoDoc.fixXmlFiles();
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
