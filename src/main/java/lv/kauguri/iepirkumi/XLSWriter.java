package lv.kauguri.iepirkumi;

import lv.kauguri.iepirkumi.data.Column;
import lv.kauguri.iepirkumi.data.Data;
import lv.kauguri.iepirkumi.data.Winner;
import lv.kauguri.iepirkumi.preferences.PreferencesManager;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static lv.kauguri.iepirkumi.data.Const.WINNERS_ID;

class XLSWriter {

    static List<Column> mainSheetColumns = Arrays.asList(
            new Column("id"),
            new Column("general.procurement_code"),
            new Column("general.name"),
            new Column("general.main_cpv.code"),
            new Column("type"),

            new Column("price_exact_eur"),
            new Column("contract_price_exact"),

            new Column("authority_name"),
            new Column("authority_reg_num"),

            new Column("exported_winner_id"),
            new Column("winners.winner.firm"),
            new Column("winners.winner.reg_num"),

            new Column("%url")
    );

    static void write(Data data, String xlsFile) {
        XSSFWorkbook workbook = new XSSFWorkbook();

        writeMainSheet(data, workbook);
        writeFullDataSheet(data, workbook);
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

    private static void writeMainSheet(Data data, XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.createSheet("Main");
        sheet.createFreezePane(0, 1);
        int rowNum = 0;

//        for(Column col : data.columns) {
//            System.out.println(col.fullName);
//        }
//        System.exit(0);

        XSSFCreationHelper createHelper = workbook.getCreationHelper();

        //cell style for hyperlinks
        //by default hyperlinks are blue and underlined
        XSSFCellStyle hlink_style = workbook.createCellStyle();
        XSSFFont hlink_font = workbook.createFont();
        hlink_font.setUnderline(org.apache.poi.ss.usermodel.Font.U_SINGLE);
        hlink_font.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
        hlink_style.setFont(hlink_font);

        XSSFRow row = sheet.createRow(rowNum++);
        int colNum = 0;

        for (Column column : mainSheetColumns) {
            XSSFCell cell = row.createCell(colNum++);
            cell.setCellValue(getColumnName(column));
        }

        for (Map<Column, String> dataRow : data.rows) {
            row = sheet.createRow(rowNum++);

            colNum = 0;
            for (Column column : mainSheetColumns) {
                XSSFCell cell = row.createCell(colNum++);

                if(column.fullName.equals("%url")) {
                    String value = getValue(dataRow, column);
                    url(cell, createHelper, hlink_style, value);
                }

                String value = getValue(dataRow, column);
                cell.setCellValue(value);
            }
        }
    }

    private static String getColumnName(Column column) {

        String name = PreferencesManager.getColumnNamesLong().getProperty(column.fullName);
        if(name == null && column.shortName != null) {
            name = PreferencesManager.getColumnNamesShort().getProperty(column.shortName);
        }
        if(name == null) {
            name = column.fullName;
        }
        if(name == null) {
            if(column.fullName.equals("%url")) {
                name = "Hipersaite";
            }
        }
        return name;
    }


    private static void writeFullDataSheet(Data data, XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.createSheet("Data");
        XSSFWorkbook wb = sheet.getWorkbook();

        XSSFCellStyle shadesOfGray = wb.createCellStyle();
        XSSFColor color = new XSSFColor(Color.GRAY);
        shadesOfGray.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        shadesOfGray.setFillForegroundColor(color);
        shadesOfGray.setWrapText(true);

        XSSFCellStyle wrap = wb.createCellStyle();
        wrap.setWrapText(true);

        sheet.createFreezePane(0, 2);
        int rowNum = 0;

        XSSFRow columnRowCotegory = sheet.createRow(rowNum++);
        XSSFRow columnRow = sheet.createRow(rowNum++);
        XSSFCell firstCell = null;
        String lastCategory = null;
        XSSFCell previousCell = null;

        int colNum = 0;
        int cellCountInCategory = 0;

        for (int i = 0; i < data.columns.length; i++) {
            sheet.setColumnWidth(i, 8000);
        }


        XSSFCell emptyCell = columnRowCotegory.createCell(0);
        emptyCell.setCellStyle(shadesOfGray);
        XSSFCell idCell = columnRow.createCell(0);
        idCell.setCellValue("id");
        colNum++;

        for (Column dataColumn : data.columns) {
            int col = colNum++;
            String currentCategory = getColumnCategory(dataColumn);

            XSSFCell currentCell = columnRowCotegory.createCell(col);

            if (currentCategory == null || !currentCategory.equals(lastCategory)) {
                if (firstCell != null && cellCountInCategory > 1) {
                    CellRangeAddress cellRangeAddress = new CellRangeAddress(
                            columnRowCotegory.getRowNum(),
                            columnRowCotegory.getRowNum(),
                            firstCell.getColumnIndex(),
                            previousCell.getColumnIndex());

                    firstCell.setCellValue(lastCategory);
                    try {
                        sheet.addMergedRegion(cellRangeAddress);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }

                firstCell = currentCell;
                lastCategory = currentCategory;
                cellCountInCategory = 1;
            } else {
                cellCountInCategory++;
            }
            previousCell = currentCell;

            XSSFCell cell = columnRow.createCell(col);
            cell.setCellValue(getColumnName(dataColumn));
            if (currentCategory == null) {
                firstCell.setCellStyle(shadesOfGray);
            }
        }


        for (int i = 0; i < data.columns.length; i++) {
            sheet.setColumnWidth(i, 8000);
        }

        for (Map<Column, String> dataRow : data.rows) {
            XSSFRow row = sheet.createRow(rowNum++);

            idCell = row.createCell(0);
            String idValue = getValue(dataRow, new Column("id"));
            idCell.setCellValue(idValue);

            colNum = 1;
            for (Column dataColumn : data.columns) {
                XSSFCell cell = row.createCell(colNum++);
                String value = getValue(dataRow, dataColumn);

                if(value != null && value.length() > 200) {
                    System.out.println(">>>"+dataColumn.fullName);
                }

                cell.setCellValue(value);
                if(value != null && value.length() > 30 && !dataColumn.fullName.endsWith("list")) {
                    cell.setCellStyle(wrap);
                    sheet.setColumnWidth(cell.getColumnIndex(), 20000);
                }
            }
        }
    }

    private static void url(XSSFCell cell, XSSFCreationHelper createHelper, XSSFCellStyle hlink_style,
                                String url) {
        cell.setCellValue(url);
        XSSFHyperlink link = createHelper.createHyperlink(HyperlinkType.URL);
        link.setAddress(url);
        cell.setHyperlink(link);
        cell.setCellStyle(hlink_style);
    }

    private static String getColumnCategory(Column column) {
        if (column.fullName == null || !column.fullName.contains(".")) {
            return null;
        } else {
            return column.fullName.split("\\.")[0];
        }
    }

    private static String getValue(Map<Column, String> dataRow, Column column) {
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

    private static String getClassifierValue(String name, String value) {
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

    private static void writeWinnersSheet(Data data, XSSFWorkbook workbook) {
        XSSFSheet sheetWinners = workbook.createSheet("Winners");
        sheetWinners.createFreezePane(0, 2);
        Set<String> columns = new HashSet<>();
        for (Winner winner : data.winners) {
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

        for (Winner winner : data.winners) {
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