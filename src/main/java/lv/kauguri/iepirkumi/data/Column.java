package lv.kauguri.iepirkumi.data;

import java.util.HashMap;
import java.util.Map;

public class Column {
    public String fullName;
    public String shortName;

    private static Map<String, Column> columns = new HashMap<>();

    public static Column createColumn(String fullName, String shortName) {
        Column column = columns.get(fullName);
        if(column == null) {
            column = new Column(fullName, shortName);
            columns.put(fullName, column);
        }
        return column;
    }

    public Column(String fullName, String shortName) {
        this.fullName = fullName;
        this.shortName = shortName;
    }

    public Column(String fullName) {
        this.fullName = fullName;
        if(fullName.contains(".")) {
            String[] nameParts = fullName.split("\\.");
            int index = nameParts.length - 1;
            this.shortName = nameParts[index];
        } else {
            this.shortName = fullName;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Column column = (Column) o;

        return fullName != null ? fullName.equals(column.fullName) : column.fullName == null;
    }

    @Override
    public int hashCode() {
        return fullName != null ? fullName.hashCode() : 0;
    }

    @Override
    public String toString() {
        return fullName;
    }
}
