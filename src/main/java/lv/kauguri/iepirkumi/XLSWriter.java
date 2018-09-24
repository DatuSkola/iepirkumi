package lv.kauguri.iepirkumi;

import lv.kauguri.iepirkumi.data.Column;
import lv.kauguri.iepirkumi.data.Data;
import lv.kauguri.iepirkumi.data.Winner;
import lv.kauguri.iepirkumi.preferences.PreferencesManager;
import lv.kauguri.iepirkumi.xls.Utils;
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

    static private
    List<Column> mainSheetColumns = Arrays.asList(
            new Column("id"),
            new Column("general.procurement_code"),
            new Column("general.name"),
            new Column("general.main_cpv.code"),
            new Column("type"),

            new Column("price_exact_eur"),
            new Column("contract_price_exact"),

            new Column("authority_name"),
            new Column("authority_reg_num"),

            new Column("winner_name"),
            new Column("exported_winner_id"),
            new Column("winners.winner.firm"),
            new Column("winners.winner.reg_num"),

            new Column("%url")
    );

    static void write(Data data, String xlsFile) {
        XSSFWorkbook workbook = new XSSFWorkbook();

        writeMainSheet(data, workbook);
//        writeFullDataSheet(data, workbook);
//        writeWinnersSheet(data, workbook);

        try {
            System.out.println(xlsFile);
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
            if(!Filter.keep(dataRow)) {
                continue;
            }

            row = sheet.createRow(rowNum++);

            colNum = 0;
            for (Column column : mainSheetColumns) {
                XSSFCell cell = row.createCell(colNum++);

                if(column.fullName.equals("%url")) {
                    String value = Utils.getValue(dataRow, column);
                    Utils.url(cell, createHelper, hlink_style, value);
                }

                String value = Utils.getValue(dataRow, column);
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
            String currentCategory = Utils.getColumnCategory(dataColumn);

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
            String idValue = Utils.getValue(dataRow, new Column("id"));
            idCell.setCellValue(idValue);

            colNum = 1;
            for (Column dataColumn : data.columns) {
                XSSFCell cell = row.createCell(colNum++);
                String value = Utils.getValue(dataRow, dataColumn);

//                if(value != null && value.length() > 200) {
//                    System.out.println(">>>"+dataColumn.fullName);
//                }

                cell.setCellValue(value);
                if(value != null && value.length() > 30 && !dataColumn.fullName.endsWith("list")) {
                    cell.setCellStyle(wrap);
                    sheet.setColumnWidth(cell.getColumnIndex(), 20000);
                }
            }
        }
    }

}