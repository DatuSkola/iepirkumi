package lv.kauguri.iepirkumi;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
    private Pattern pattern;

    public Regex(String strPattern) {
        pattern = Pattern.compile(strPattern);
    }

    public List<String> get(String line) {
        Matcher m = pattern.matcher(line);
        List<String> result = new ArrayList<>();
        while (m.find()) {
            String a = m.group(1);
            result.add(a);
        }
        return result;
    }
}
