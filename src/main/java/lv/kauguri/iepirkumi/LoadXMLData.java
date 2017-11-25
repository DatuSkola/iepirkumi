package lv.kauguri.iepirkumi;

import org.apache.commons.io.IOUtils;
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
import java.util.function.BiConsumer;

class LoadXMLData {

    static Data loadXmls(File xmlDirectory) throws IOException {
        File[] xmls = xmlDirectory.listFiles();
//        xmls = Arrays.copyOf(xmls, 10);

        Set<String> columns = new HashSet<>();
        List<Map<String, String>> rows = new ArrayList<>();

        for (File xmlFile : xmls) {
            visitFiles(xmlFile, (name, node) -> columns.add(name));
        }

        for (File xmlFile : xmls) {
            Map<String, String> values = new HashMap<>();
            visitFiles(xmlFile, (name, content) -> values.put(name, content));
            rows.add(values);
        }

        String[] columnArray = columns.toArray(new String[]{});
        Arrays.sort(columnArray);

        return new Data(columnArray, rows);
    }

    private static void visitFiles(File file, BiConsumer<String, String> function2) {
        Document doc = readXml(file);
        if (doc == null) {
            return;
        }
        NodeList nodeList = doc.getChildNodes().item(0).getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            visitNode(node, null, function2);
        }
    }

    private static void visitNode(Node node, String parentName, BiConsumer<String, String> function2) {
        if (node.getNodeName().equals("#text")) {
            return;
        }
        NodeList childNodes = node.getChildNodes();

        if (parentName != null) {
            parentName = parentName + ".";
        } else {
            parentName = "";
        }

        if (childNodes.getLength() > 1) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                String name = parentName + node.getNodeName();
                visitNode(childNode, name, function2);
            }
        } else {
            function2.accept(parentName + node.getNodeName(), node.getTextContent());
        }
    }


    public static void main(String[] args) {
        readXml(new File("ftp_files\\xmls\\2017\\1\\480009.xml"));
    }

    private static Document readXml(File file) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            is = new ReplacingInputStream(is, "&".getBytes(), "&amp;".getBytes());

//            BufferedReader br = new BufferedReader(new InputStreamReader(is));
//
//            String result = "";
//            String line;
//            while ((line = br.readLine()) != null) {
//                System.out.println(line);
//            }
//
//            br.close();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;

            db = dbf.newDocumentBuilder();
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
