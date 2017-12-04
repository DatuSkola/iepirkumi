package lv.kauguri.iepirkumi;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static lv.kauguri.iepirkumi.VisitFile.WINNERS_ID;


class XLSWriter {

    static void write(Data data, String xlsFile) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Data");
        int rowNum = 0;

        XSSFRow row = sheet.createRow(rowNum++);
        int colNum = 0;
        for (Column dataColumn : data.columns) {
            XSSFCell cell = row.createCell(colNum++);
            cell.setCellValue(dataColumn.fullName);
        }

        for (Map<Column, String> dataRow : data.rows) {
            row = sheet.createRow(rowNum++);

            colNum = 0;
            for (Column dataColumn : data.columns) {
                XSSFCell cell = row.createCell(colNum++);
                String value = dataRow.get(dataColumn);
                try {
                    value = prepareValue(value);
                    value = getClassifierValue(dataColumn, value);
                    cell.setCellValue(value);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }

        writeWinnersSheet(data, workbook);

        try {
            FileOutputStream outputStream = new FileOutputStream(xlsFile);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getClassifierValue(Column dataColumn, String value) {
        if(value != null && value.length() > 0) {
            if(dataColumn.shortName.endsWith("country")) {
                String classifierVaule = PreferencesManager.getCountries().getProperty(value);
                return classifierVaule;
            } else if(dataColumn.shortName.endsWith("currency")) {
                String classifierVaule = PreferencesManager.getCurrencies().getProperty(value);
                return classifierVaule;
            } else if(dataColumn.shortName.endsWith("language")) {
                String classifierVaule = PreferencesManager.getLanguages().getProperty(value);
                return classifierVaule;
            }
        }
        return value;
    }

    private static void writeWinnersSheet(Data data, XSSFWorkbook workbook) {
        XSSFSheet sheetWinners = workbook.createSheet("Winners");
        Set<String> columns = new HashSet<>();
        for(Winner winner : data.winners) {
            columns.addAll(winner.attributes.keySet());
        }

        String[] columnsArray = columns.toArray(new String[]{});

        int rowNum = 0;
        XSSFRow row = sheetWinners.createRow(rowNum++);

        int colNum = 0;
        XSSFCell cell = row.createCell(colNum++);
        cell.setCellValue(WINNERS_ID);

        for (String dataColumn : columnsArray) {
            cell = row.createCell(colNum++);
            cell.setCellValue(dataColumn);
        }

        for(Winner winner : data.winners) {
            row = sheetWinners.createRow(rowNum++);
            colNum = 0;
            cell = row.createCell(colNum++);
            cell.setCellValue(winner.id);

            for (String column : columnsArray) {
                cell = row.createCell(colNum++);
                String value = winner.attributes.get(column);
                value = prepareValue(value);
                cell.setCellValue(value);
            }

        }
    }

    private static String prepareValue(String value) {
        if(value == null) {
            return null;
        }
        int max = SpreadsheetVersion.EXCEL2007.getMaxTextLength();
        if(value.length() > max) {
            value = value.substring(0, max);
        }
        if(value.contains("&")) {
            value = value.replace("&amp;", "&");
            value = value.replace("&quot;", "\"");
            value = value.replace("&apos;", "\'");
            value = value.replace("&ndash;", "–");
            value = value.replace("&hellip;", "…");
            value = value.replace("&bull;", "•");
            value = value.replace("&lt;", "<");
            value = value.replace("&gt;", ">");
            value = value.replace("&ldquo;", "“");
            value = value.replace("&rdquo;", "”");
        }
        return value;
    }


}
