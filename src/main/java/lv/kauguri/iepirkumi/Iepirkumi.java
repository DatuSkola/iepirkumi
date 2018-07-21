package lv.kauguri.iepirkumi;

import lv.kauguri.iepirkumi.data.Data;
import lv.kauguri.iepirkumi.data.DateRange;
import lv.kauguri.iepirkumi.files.FileOperations;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;

import static lv.kauguri.iepirkumi.files.FileOperations.*;
import static lv.kauguri.iepirkumi.files.FileOperations.getDir;

public class Iepirkumi {

    public static String SEP = File.separator;
    public static String WORK_DIR = SEP + "data" + SEP + "iepirkumi" + SEP;
    public static String ARCHIVES_DIR = WORK_DIR + "arch" + SEP;
    public static String XML_DIR = WORK_DIR + "xmls" + SEP;
    public static String TMP_DIR = WORK_DIR + "tmp" + SEP;
    public static String XLS_DIR = WORK_DIR + "xls" + SEP;
    public static String FIX_BAT = "fix.bat";

    public static void main(String[] args) throws IOException, InterruptedException {
        loadData();

        XMLtoDoc.loadXmls(data -> XLSWriter.write(data, XLS_DIR + SEP + data.year + "_" + data.month + ".xlsx"));
    }

    static void loadData() throws IOException {
        FileOperations.run("rm -rf " + WORK_DIR);
        createIfNeeded(WORK_DIR);
        createIfNeeded(ARCHIVES_DIR);

        DateRange dateRange = new DateRange(2018, 7);
        (new DownloadFromIUB()).download(ARCHIVES_DIR, dateRange);
        recreateDirectories();

        visitEachSubSubDirectory2(ARCHIVES_DIR)
            .forEach(dir -> {
                String year = dir.year;
                String month = dir.month;
                File archiveDir = new File(ARCHIVES_DIR + SEP + year + SEP + month);
                File xmlDir = new File(XML_DIR + SEP + year + SEP + month);

                for(File achiveFile : archiveDir.listFiles()) {
                    UnpacArchivesToXml.extractFile(achiveFile, xmlDir);
                }

                run(FIX_BAT +  " "+ xmlDir);

                Data data = new Data(year, month);
                for (File xmlFile : xmlDir.listFiles()) {
                    Document doc = XMLtoDoc.readXml(xmlFile);
                    if(doc == null) {
                        continue;
                    }
                    (new DocToData(doc, data)).visitFiles();
                }
                data.build();

                XLSWriter.write(data, XLS_DIR + SEP + data.year + "_" + data.month + ".xlsx");
            });
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
