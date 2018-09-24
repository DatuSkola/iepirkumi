package lv.kauguri.iepirkumi;

import lv.kauguri.iepirkumi.data.Data;
import lv.kauguri.iepirkumi.data.Winner;
import lv.kauguri.iepirkumi.xls.Utils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.HashSet;
import java.util.Set;

import static lv.kauguri.iepirkumi.data.Const.WINNERS_ID;

public class WinnerSheet {

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
                value = Utils.prepareValue(value);
                cell.setCellValue(value);
            }

        }
    }
}
