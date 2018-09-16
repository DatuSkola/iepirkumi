package lv.kauguri.iepirkumi;

import lv.kauguri.iepirkumi.data.Column;
import lv.kauguri.iepirkumi.data.Data;
import lv.kauguri.iepirkumi.data.Winner;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

import static lv.kauguri.iepirkumi.data.Const.WINNERS_ID;

public class DocToData {
    Map<Column, String> values;
    List<Winner> winners;
    Document sourceDocument;
    Data resultData;

    public DocToData(Document sourceDocument, Data resultData) {
        this.sourceDocument = sourceDocument;
        this.resultData = resultData;
        this.values = new HashMap<>();
        this.winners = new ArrayList<>();
    }

    void visitFiles() {
        NodeList nodeList = sourceDocument.getChildNodes().item(0).getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            visitNode(values, node, "");
        }

        resultData.addRow(values);
        resultData.addWinners(winners);
    }

    private void visitNode(Map<Column, String> map2, Node node, String parentName) {
        String nodeName = node.getNodeName();

        if(nodeName.contains("winner_name")) {
            StringBuilder sb = new StringBuilder();
            printNode(sb, node, " ");
        }

        if(nodeName.equals("winner_list")) {
            for(int i = 0; i < node.getChildNodes().getLength(); i++) {
                Map<Column, String> map = new HashMap<>();
                Node winnerNode = node.getChildNodes().item(i);
                visitChildren(map, winnerNode, parentName, "winner" );

                String winnerName = parentName;
                if(!winnerName.isEmpty()) {
                    winnerName += ".";
                }
                winnerName += "winner.winner_name";

                if(map.get(new Column(winnerName)) != null) {
                    String value = map.get(new Column(winnerName));
                    map2.put(Column.createColumn("winner_name", "winner_name"), value);
                }
            }
            processWinnerList(node);
            return;
        }

        if (nodeName.equals("#text")) {
            return;
        }

        visitChildren(map2, node, parentName, nodeName);
    }

    private void printNode(StringBuilder stringBuilder, Node rootNode, String spacer) {
        stringBuilder.append(spacer + rootNode.getNodeName() + " -> " + rootNode.getNodeValue());
        NodeList nl = rootNode.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++)
            printNode(stringBuilder, nl.item(i), spacer + "   ");
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

    private void visitChildren(Map<Column, String> map, Node node, String parentName, String nodeName) {
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
                visitNode(map, childNode, name);
            }
        } else {
            map.put(Column.createColumn(parentName + nodeName, nodeName), node.getTextContent());
        }
    }

    private void processWinnerList(Node node) {
        List<Winner> winnersTmp = processWinnerList2(node);
        winners.addAll(winnersTmp);

        String winnersIds = "";
        for(Winner winner : winnersTmp) {
            winnersIds += winner.id + ",";
        }
        values.put(Column.createColumn(WINNERS_ID, WINNERS_ID), winnersIds);
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
