package lv.kauguri.iepirkumi;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static lv.kauguri.iepirkumi.FileOperations.getDir;
import static lv.kauguri.iepirkumi.FileOperations.visitEachSubSubDirectory;
import static lv.kauguri.iepirkumi.Iepirkumi.*;

class LoadXMLData {

    static void fixXmlFiles() throws IOException {
        visitEachSubSubDirectory(XML_DIR, (year, month) -> {
            run(FIX_BAT +  " "+ getDir(XML_DIR, year, month));
        });
    }

    static void loadXmls() throws IOException {
        visitEachSubSubDirectory(XML_DIR, (year, month) -> {
            File xmlDir = new File(getDir(XML_DIR, year, month));
            Data data = new Data();

            for (File xmlFile : xmlDir.listFiles()) {
                Document doc = readXml(xmlFile);
                if(doc == null) {
                    continue;
                }
                (new VisitFile(doc, data)).visitFiles();
            }
            data.build();

            XLSWriter.write(data, XLS_DIR + SEP + year + "_" + month + ".xlsx");
        });
    }

    static void run(String command) {
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

    private static Document readXml(File file) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
//            is = new ReplacingInputStream(is, "&".getBytes(), "&amp;".getBytes());

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);
            return doc;

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();

        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
}