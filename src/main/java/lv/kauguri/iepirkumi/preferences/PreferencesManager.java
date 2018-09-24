package lv.kauguri.iepirkumi.preferences;

import lv.kauguri.iepirkumi.files.ReadCSV;
import lv.kauguri.iepirkumi.util.Regex;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;


public class PreferencesManager {

    private static String COUNTRIES_FILE = "classifiers/countries";
    private static String CURRENCIES_FILE = "classifiers/currency";
    private static String LANGUAGES_FILE = "classifiers/languages";
    private static String TYPES_FILE = "classifiers/types";
    private static String CPV_CODES = "classifiers/cpv_main_v1.0.csv";
    private static String COLUMN_LONG_NAMES = "classifiers/long.csv";
    private static String COLUMN_SHORT_NAMES = "classifiers/short.csv";

    private static Properties countries;
    private static Properties currencies;
    private static Properties languages;
    private static Properties types;
    private static Properties columnNamesLong;
    private static Properties columnNamesShort;
    private static Map<String, CpvCode> cpvCodes;

//    public static void main(String[] args) {
//
//    }

//    static void write() throws IOException {
//        Properties applicationProps = new Properties();
//        applicationProps.put("one", "1");
//        applicationProps.put("two", "1");
//        applicationProps.put("three", "1");
//
//        try(FileOutputStream out = new FileOutputStream("classifiers/props")) {
//            applicationProps.store(out, "");
//        }
//
//    }

    public static Map<String, CpvCode> getCPVCodes() {
        if(cpvCodes == null) {
            cpvCodes = new HashMap<>();
            Regex regex = new Regex("\"([^\"]*)\"");
            ReadCSV.read(CPV_CODES, line -> {
                List<String> str = regex.get(line);
                cpvCodes.put(str.get(0),
                        new CpvCode(
                                str.get(0),
                                str.get(1),
                                str.get(2),
                                str.size() >= 4 ? str.get(3) : null));
                return null;
            });
        }
        return cpvCodes;
    }

    public static Properties getCountries() {
        if (countries == null) {
            countries = read(COUNTRIES_FILE);
        }
        return countries;
    }

    public static Properties getCurrencies() {
        if (currencies == null) {
            currencies = read(CURRENCIES_FILE);
        }
        return currencies;
    }

    public static Properties getLanguages() {
        if (languages == null) {
            languages = read(LANGUAGES_FILE);
        }
        return languages;
    }

    public static Properties getTypes() {
        if (types == null) {
            types = read(TYPES_FILE);
        }
        return types;
    }

    public static Properties getColumnNamesLong() {
        if (columnNamesLong == null) {
            columnNamesLong = read(COLUMN_LONG_NAMES);
        }
        return columnNamesLong;
    }

    public static Properties getColumnNamesShort() {
        if (columnNamesShort == null) {
            columnNamesShort = read(COLUMN_SHORT_NAMES);
        }
        return columnNamesShort;
    }

    static Properties read(String fileName) {
        Properties properties = new Properties();
        try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(fileName), Charset.forName("UTF-8"))) {
            properties.load(inputStreamReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }


}
