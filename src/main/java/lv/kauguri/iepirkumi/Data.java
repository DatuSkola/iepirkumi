package lv.kauguri.iepirkumi;

import java.util.*;

class Data {
    String[] columns;
    List<Map<String, String>> rows;

    Data(String[] columns, List<Map<String, String>> rows) {
        this.columns = columns;
        this.rows = rows;
    }
}
