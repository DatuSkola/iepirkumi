package lv.kauguri.iepirkumi;

import lv.kauguri.iepirkumi.data.Column;
import lv.kauguri.iepirkumi.util.Regex;

import java.util.Map;

public class Filter {

    static private Column column = new Column("authority_name");

    Regex regex = new Regex("");

    static boolean keep(Map<Column, String> dataRow) {
//        String value = dataRow.get(column);
//        if(value != null
//                && (value.contains("dome")
//                    || value.contains("novads") ) ) {
//            return true;
//        }
//        return false;
        return true;
    }
}
