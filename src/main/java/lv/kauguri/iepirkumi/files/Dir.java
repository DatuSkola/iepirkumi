package lv.kauguri.iepirkumi.files;

public class Dir {
    public String year;
    public String month;

    public Dir(String year, String month) {
        this.year = year;
        this.month = month;
    }

    @Override
    public String toString() {
        return "a_" + year +"_"+ month;
    }
}