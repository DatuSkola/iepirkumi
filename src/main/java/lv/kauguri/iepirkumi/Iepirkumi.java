package lv.kauguri.iepirkumi;

import org.codehaus.plexus.archiver.ArchiverException;

import java.io.File;
import java.io.IOException;

import static lv.kauguri.iepirkumi.FileOperations.createIfNeeded;

public class Iepirkumi {

    static String SEP = File.separator;
    static String WORK_DIR = SEP + "data" + SEP + "iepirkumi" + SEP;
    static String ARCHIVES_DIR = WORK_DIR + "arch" + SEP;
    static String XML_DIR = WORK_DIR + "xmls" + SEP;
    static String TMP_DIR = WORK_DIR + "tmp" + SEP;
    static String XLS_DIR = WORK_DIR + "xls" + SEP;

    public static void main(String[] args) throws IOException, InterruptedException {
//        FileOperations.rmrf();
//        DateRange dateRange = new DateRange(2017, 8);
//        DownloaderFTP.download(dateRange);
        extractAll(false);
    }


    static void extractAll(boolean unarchive) throws IOException {
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

                if(unarchive) {
                    for(File archive : monthDir.listFiles() ) {
                        try {
                            UnArchiver.extract(archive, xmlMonthDir);
                        } catch(ArchiverException e) {
                            e.printStackTrace();
                        }
                    }
                }

                Data data = LoadXMLData.loadXmls(xmlMonthDir);
                XLSWriter.write(data, xlsMonthDir + SEP + yearDir.getName() + "_" + monthDir.getName() + ".xlsx");
            }
        }
    }

}
