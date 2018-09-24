package lv.kauguri.iepirkumi.xls;

import lv.kauguri.iepirkumi.data.Column;
import lv.kauguri.iepirkumi.preferences.PreferencesManager;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;

import java.util.Map;

public class Utils {
    
    public static void url(XSSFCell cell, XSSFCreationHelper createHelper, XSSFCellStyle hlink_style,
                            String url) {
        cell.setCellValue(url);
        XSSFHyperlink link = createHelper.createHyperlink(HyperlinkType.URL);
        link.setAddress(url);
        cell.setHyperlink(link);
        cell.setCellStyle(hlink_style);
    }

    public static String getColumnCategory(Column column) {
        if (column.fullName == null || !column.fullName.contains(".")) {
            return null;
        } else {
            return column.fullName.split("\\.")[0];
        }
    }

    public static String getValue(Map<Column, String> dataRow, Column column) {
        String value;
        if(column.fullName.equals("%url")) {
            String idValue = dataRow.get(new Column("id"));
            value = "https://pvs.iub.gov.lv/show/" + idValue;

            return value;
        }

        value = dataRow.get(column);
        try {
            value = prepareValue(value);
            value = getClassifierValue(column.fullName, value);
            return value;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getClassifierValue(String name, String value) {
        if (value != null && value.length() > 0) {
            if (name.endsWith("country")) {
                String classifierVaule = PreferencesManager.getCountries().getProperty(value);
                return classifierVaule;
            } else if (name.endsWith("currency")) {
                String classifierVaule = PreferencesManager.getCurrencies().getProperty(value);
                return classifierVaule;
            } else if (name.endsWith("language")) {
                String classifierVaule = PreferencesManager.getLanguages().getProperty(value);
                return classifierVaule;
            } else if (name.equals("type")) {
                String classifierVaule = PreferencesManager.getTypes().getProperty(value);
                return classifierVaule.split(";")[2];
            } else if (name.endsWith(".code")) {
                String classifierVaule = PreferencesManager.getCPVCodes().get(value).lv;
                return classifierVaule;
            }
        }
        return value;
    }

    public static String prepareValue(String value) {
        if (value == null) {
            return null;
        }
        int max = SpreadsheetVersion.EXCEL2007.getMaxTextLength();
        if (value.length() > max) {
            value = value.substring(0, max);
        }
        if (value.contains("&")) {
            value = value.replace("&amp;", "&");
            value = value.replace("&quot;", "\"");
            value = value.replace("&apos;", "\'");
            value = value.replace("&ndash;", "–");
            value = value.replace("&hellip;", "…");
            value = value.replace("&bull;", "•");
            value = value.replace("&lt;", "<");
            value = value.replace("&gt;", ">");
            value = value.replace("&ldquo;", "“");
        }
        return value;
    }
}
