package lv.kauguri.iepirkumi;

import lv.kauguri.iepirkumi.data.Data;
import lv.kauguri.iepirkumi.data.DateRange;
import lv.kauguri.iepirkumi.files.Dir;
import lv.kauguri.iepirkumi.files.FileOperations;
import lv.kauguri.iepirkumi.reactive.ChainedExecutor;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static lv.kauguri.iepirkumi.files.FileOperations.*;

public class Iepirkumi {

    public static String SEP = File.separator;
    public static String WORK_DIR = SEP + "data" + SEP + "iepirkumi" + SEP;
    public static String ARCHIVES_DIR = WORK_DIR + "arch" + SEP;
    public static String XML_DIR = WORK_DIR + "xmls" + SEP;
    public static String TMP_DIR = WORK_DIR + "tmp" + SEP;
    public static String XLS_DIR = WORK_DIR + "xls" + SEP;
    public static String FIX_BAT = "fix.bat";

    public static boolean NEED_TO_DOWNLOAD = true;

    static ChainedExecutor chainedExecutor = new ChainedExecutor();

    public static void main(String[] args) {
//        chainedExecutor.start(Iepirkumi::download);
        chainedExecutor.start(Iepirkumi::recreateXLS);
        chainedExecutor.waitToFinish();
    }

    static void recreateXLS() {
        FileOperations.run("rm -rf " + XLS_DIR);
        try {
            createIfNeeded(XLS_DIR);
            List<Dir> list = visitEachSubSubDirectory2(XML_DIR);
            for (Dir dir : list) {
                chainedExecutor.returns(dir, Iepirkumi::buildXLS);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void download(Object o) {
        FileOperations.run("rm -rf " + WORK_DIR);
        try {
            createIfNeeded(WORK_DIR);
            createIfNeeded(ARCHIVES_DIR);

            DateRange dateRange = new DateRange(2018, 7);
            (new DownloadFromIUB()).download(ARCHIVES_DIR, dateRange);
            recreateDirectories();

            List<Dir> list = visitEachSubSubDirectory2(ARCHIVES_DIR);
            for (Dir dir : list) {
                chainedExecutor.returns(dir, Iepirkumi::unarchive);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void unarchive(Dir dir) {
        String year = dir.year;
        String month = dir.month;
        File archiveDir = new File(ARCHIVES_DIR + SEP + year + SEP + month);
        File xmlDir = new File(XML_DIR + SEP + dir.year + SEP + dir.month);

        for (File archiveFile : archiveDir.listFiles()) {
            UnpacArchivesToXml.extractFile(archiveFile, xmlDir);
        }
        run(FIX_BAT + " " + xmlDir);
        chainedExecutor.returns(dir, Iepirkumi::buildXLS);
    }

    static void buildXLS(Dir dir) {
        try {
            File xmlDir = new File(XML_DIR + SEP + dir.year + SEP + dir.month);
            Data data = new Data(dir.year, dir.month);
            for (File xmlFile : xmlDir.listFiles()) {
                createIfNeeded(XLS_DIR + SEP + dir.year);
                Document doc = XMLtoDoc.readXml(xmlFile);
                if (doc == null) {
                    continue;
                }
                (new DocToData(doc, data)).visitFiles();
            }
            data.build();

            chainedExecutor.returns(data, Iepirkumi::writeXLS);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    static void writeXLS(Data data) {
        XLSWriter.write(data, XLS_DIR + data.year + SEP + data.month + ".xlsx");
    }


    static class Files {
        Dir dir;
        File archiveFile;

        public Files(Dir dir, File archiveFile) {
            this.dir = dir;
            this.archiveFile = archiveFile;
        }
    }

    static void recreateDirectories() throws IOException {
        File dir = new File(ARCHIVES_DIR);

        createIfNeeded(XML_DIR);
        createIfNeeded(TMP_DIR);
        createIfNeeded(XLS_DIR);

        for (File yearDir : dir.listFiles()) {
            createIfNeeded(XML_DIR + SEP + yearDir.getName());
            createIfNeeded(XLS_DIR + SEP + yearDir.getName());

            for (File monthDir : yearDir.listFiles()) {
                File xmlMonthDir = new File(XML_DIR + SEP + yearDir.getName() + SEP + monthDir.getName());
                File xlsMonthDir = new File(XLS_DIR + SEP + yearDir.getName() + SEP + monthDir.getName());
                createIfNeeded(xmlMonthDir);
                createIfNeeded(xlsMonthDir);
            }
        }
    }


}
