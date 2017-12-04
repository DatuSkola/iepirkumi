package lv.kauguri.iepirkumi;

import java.util.*;

import static lv.kauguri.iepirkumi.VisitFile.WINNERS_ID;


class Data {
    Column[] columns;
    List<Map<Column, String>> rows;
    List<Winner> winners;

    public Data() {
        rows = new ArrayList<>();
        winners = new ArrayList<>();
    }

    void addRow(Map<Column, String> row) {
        rows.add(row);
    }

    void addWinners(List<Winner> winners) {
        this.winners.addAll(winners);
    }

    void build() {
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
