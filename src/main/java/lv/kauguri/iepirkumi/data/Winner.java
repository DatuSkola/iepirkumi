package lv.kauguri.iepirkumi.data;

import java.util.HashMap;
import java.util.Map;

public class Winner {
    public Map<String, String> attributes = new HashMap<>();
    public String id;

    public void put(String column, String value) {
        attributes.put(column, value);
    }
}
