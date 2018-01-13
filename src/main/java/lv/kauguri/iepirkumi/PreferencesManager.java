package lv.kauguri.iepirkumi;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class PreferencesManager {

    private static String COUNTRIES_FILE = "classifiers/countries";
    private static String CURRENCIES_FILE = "classifiers/currency";
    private static String LANGUAGES_FILE = "classifiers/languages";
    private static String TYPES_FILE = "classifiers/types";
    private static Properties countries;
    private static Properties currencies;
    private static Properties languages;
    private static Properties types;

    public static void main(String[] args) throws IOException {

    }

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

    static Properties getCountries() {
        if(countries == null) {
            countries = read(COUNTRIES_FILE);
        }
        return countries;
    }

    static Properties getCurrencies() {
        if(currencies == null) {
            currencies = read(CURRENCIES_FILE);
        }
        return currencies;
    }

    static Properties getLanguages() {
        if(languages == null) {
            languages = read(LANGUAGES_FILE);
        }
        return languages;
    }

    static Properties getTypes() {
        if(types == null) {
            types = read(TYPES_FILE);
        }
        return types;
    }


    static Properties read(String fileName) {
        Properties properties = new Properties();
        try(InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(fileName), Charset.forName("UTF-8"))) {
            properties.load(inputStreamReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }


}
