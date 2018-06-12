package lv.kauguri.iepirkumi.data;

import java.util.Calendar;

public class DateRange {
    public int fromYear;
    public int fromMonth;
    public int toYear;
    public int toMonth;

    public DateRange(int fromYear, int fromMonth) {
        this.fromYear = fromYear;
        this.fromMonth = fromMonth;
        Calendar calendar = Calendar.getInstance();

        this.toYear = calendar.get(Calendar.YEAR);
        this.toMonth = calendar.get(Calendar.MONTH) + 1;
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
