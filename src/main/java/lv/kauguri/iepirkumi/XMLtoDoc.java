package lv.kauguri.iepirkumi;

import lv.kauguri.iepirkumi.data.Data;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import static lv.kauguri.iepirkumi.files.FileOperations.getDir;
import static lv.kauguri.iepirkumi.files.FileOperations.run;
import static lv.kauguri.iepirkumi.files.FileOperations.visitEachSubSubDirectory;
import static lv.kauguri.iepirkumi.Iepirkumi.*;

class XMLtoDoc {

    static void fixXmlFiles() {
        visitEachSubSubDirectory(XML_DIR, (year, month) -> {
            run(FIX_BAT +  " "+ getDir(XML_DIR, year, month));
        });
    }

    static void loadXmls(Consumer<Data> consumer) {
        visitEachSubSubDirectory(XML_DIR, (year, month) -> {
            File xmlDir = new File(getDir(XML_DIR, year, month));
            Data data = new Data(year, month);

            for (File xmlFile : xmlDir.listFiles()) {
                Document doc = readXml(xmlFile);
                if(doc == null) {
                    continue;
                }
                (new DocToData(doc, data)).visitFiles();
            }
            data.build();

            consumer.accept(data);
        });
    }

    private static Document readXml(File file) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);

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