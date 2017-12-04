package lv.kauguri.iepirkumi;

import org.apache.poi.util.ReplacingInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;

class LoadXMLData {

    static Data loadXmls(File xmlDirectory) throws IOException {
        File[] xmls = xmlDirectory.listFiles();
        Data data = new Data();

        for (File xmlFile : xmls) {
            Document doc = readXml(xmlFile);
            if(doc == null) {
                continue;
            }
            (new VisitFile(doc, data)).visitFiles();
        }

        data.build();
        return data;
    }


    private static Document readXml(File file) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            is = new ReplacingInputStream(is, "&".getBytes(), "&amp;".getBytes());

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