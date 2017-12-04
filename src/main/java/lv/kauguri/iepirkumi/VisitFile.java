package lv.kauguri.iepirkumi;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

public class VisitFile {
    static String WINNERS_ID = "exported_winner_id";
    Map<Column, String> values;
    List<Winner> winners;
    Document doc;
    Data data;

    public VisitFile(Document doc, Data data) {
        this.doc = doc;
        this.data = data;
        this.values = new HashMap<>();
        this.winners = new ArrayList<>();
    }

    void visitFiles() {
        NodeList nodeList = doc.getChildNodes().item(0).getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            visitNode(node, "");
        }

        data.addRow(values);
        data.addWinners(winners);
    }

    private void visitNode(Node node, String parentName) {
        String nodeName = node.getNodeName();

        if(nodeName.equals("winner_list")) {
            processWinnerList(node);
            return;
        }

        if (nodeName.equals("#text")) {
            return;
        }

        if(nodeName.endsWith("list")) {
            String value = toString(node).trim();
            values.put(new Column(parentName + nodeName, nodeName), value);
            return;
        }

        visitChildren(node, parentName, nodeName);
    }

    private String toString(Node node) {
        NodeList nodeList = node.getChildNodes();
        if(nodeList.getLength() > 1) {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < nodeList.getLength(); i++) {
                Node childNode = nodeList.item(i);
                sb.append(toString(childNode));
            }
            return sb.toString();
        } else {
            return node.getTextContent();
        }
    }

    private void visitChildren(Node node, String parentName, String nodeName) {
        NodeList childNodes = node.getChildNodes();

        if (parentName != null && parentName.length() > 0) {
            parentName = parentName + ".";
        } else {
            parentName = "";
        }

        if (childNodes.getLength() > 1) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                String name = parentName + nodeName;
                visitNode(childNode, name);
            }
        } else {
            values.put(new Column(parentName + nodeName, nodeName), node.getTextContent());
        }
    }

    private void processWinnerList(Node node) {
        List<Winner> winnersTmp = processWinnerList2(node);
        winners.addAll(winnersTmp);

        String winnersIds = "";
        for(Winner winner : winnersTmp) {
            winnersIds += winner.id + ",";
        }
        values.put(new Column(WINNERS_ID, WINNERS_ID), winnersIds);
    }

    private List<Winner> processWinnerList2(Node node) {
        List<Winner> winners = new ArrayList<>();

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node winnerNode = childNodes.item(i);
            if(winnerNode.getNodeName().equals("#text")) {
                continue;
            }

            Winner winner = new Winner();
            winners.add(winner);

            NodeList winnerAttributes = winnerNode.getChildNodes();
            for (int j = 0; j < winnerAttributes.getLength(); j++) {
                Node winnerAttr = winnerAttributes.item(j);
                if(winnerAttr.getNodeName().equals("#text")) {
                    continue;
                }
                winner.put(winnerAttr.getNodeName(), winnerAttr.getTextContent());
            }

            setID(winner);
        }

        return winners;
    }

    private void setID(Winner winner) {
        String id = winner.attributes.get("winner_reg_num");
        if(id != null && !id.isEmpty()) {
            winner.id = id;
        } else {
            winner.id = "fake_id_"+ winner.hashCode();
        }
    }
}
