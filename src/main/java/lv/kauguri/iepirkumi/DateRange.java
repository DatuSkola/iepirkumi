package lv.kauguri.iepirkumi;

import java.util.Calendar;

class DateRange {
    int fromYear;
    int fromMonth;
    int toYear;
    int toMonth;

    DateRange(int fromYear, int fromMonth) {
        this.fromYear = fromYear;
        this.fromMonth = fromMonth;
        Calendar calendar = Calendar.getInstance();

        this.toYear = calendar.get(Calendar.YEAR);
        this.toMonth = calendar.get(Calendar.MONTH) + 1;
    }

    public static void main(String[] args) {
        new DateRange(2017, 11);
    }

    public int getFromMonth(int year) {
        if (year == fromYear) {
            return fromMonth;
        }
        return 1;
    }

    public int getToMonth(int year) {
        if (year == toYear) {
            return toMonth;
        }
        return 12;
    }
}
