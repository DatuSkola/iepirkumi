package lv.kauguri.iepirkumi;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;


class XLSWriter {

    static void write(Data data, String xlsFile) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Datatypes in Java");

        int rowNum = 0;

        XSSFRow row = sheet.createRow(rowNum++);
        int colNum = 0;
        for (String dataColumn : data.columns) {
            XSSFCell cell = row.createCell(colNum++);
            cell.setCellValue(dataColumn);
        }

        for (Map<String, String> dataRow : data.rows) {
            row = sheet.createRow(rowNum++);

            colNum = 0;
            for (String dataColumn : data.columns) {
                XSSFCell cell = row.createCell(colNum++);
                String value = dataRow.get(dataColumn);
                try {
                    value = substringToFit(value);
                    cell.setCellValue(value);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }

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

    private static String substringToFit(String value) {
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
