package lv.kauguri.iepirkumi.data;

import java.util.*;

import static lv.kauguri.iepirkumi.data.Const.WINNERS_ID;

public class Data {
    public Column[] columns;
    public List<Map<Column, String>> rows;
    public List<Winner> winners;
    public String year;
    public String month;

    public Data(String year, String month) {
        this.rows = new ArrayList<>();
        this.winners = new ArrayList<>();
        this.year = year;
        this.month = month;
    }

    public void addRow(Map<Column, String> row) {
        rows.add(row);
    }

    public void addWinners(List<Winner> winners) {
        this.winners.addAll(winners);
    }

    public void build() {
        Set<Column> columns = new HashSet<>();
        for(Map<Column, String> row : rows) {
            columns.addAll(row.keySet());
        }

        Column[] columnArray = columns.toArray(new Column[]{});
        Arrays.sort(columnArray, (o1, o2) -> {
            if(o1.fullName.equals(WINNERS_ID)) {
                return -1;
            }
            if(o2.fullName.equals(WINNERS_ID)) {
                return 1;
            }
            return o1.fullName.compareTo(o2.fullName);
        });

        this.columns = columnArray;
    }



}
