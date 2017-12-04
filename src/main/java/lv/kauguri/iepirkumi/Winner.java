package lv.kauguri.iepirkumi;

import java.util.HashMap;
import java.util.Map;

public class Winner {
    Map<String, String> attributes = new HashMap<>();
    String id;

    void put(String column, String value) {
        attributes.put(column, value);
    }

}
