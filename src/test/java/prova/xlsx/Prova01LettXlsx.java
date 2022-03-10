package prova.xlsx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import sm.readfatt.sys.Utils;

public class Prova01LettXlsx {

  private static final String CSZ_XLSX = "dati/Fattura_Templ.xlsx";

  public static void main(String[] args) {

    Prova01LettXlsx app = new Prova01LettXlsx();
    app.doTheJob(CSZ_XLSX);
  }

  private void doTheJob(String cszXlsx) {
    try (Workbook wkb = new XSSFWorkbook(new FileInputStream(new File(cszXlsx)))) {
      leggiWorkb(wkb);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void leggiWorkb(Workbook wkb) {
    Sheet sheet = wkb.getSheetAt(0);
    System.out.printf("\nSheet:%s\n", sheet.getSheetName());
    Map<Integer, List<String>> data = new HashMap<>();
    int iRow = 0;
    for (Row row : sheet) {
      data.put(iRow, new ArrayList<String>());
      int iCol = 0;
      for (Cell cell : row) {
        switch (cell.getCellType()) {

          case STRING:
            String sz = cell.getRichStringCellValue().getString();
            data.get(Integer.valueOf(iRow)).add(sz);
            System.out.printf("%d:%d:s=%s\t", iRow, iCol, sz);
            break;

          case NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) {
              Date dt = cell.getDateCellValue();
              data.get(iRow).add(dt + "");
              System.out.printf("%d:%d:dt=%s\t", iRow, iCol, Utils.s_fmtDMY4.format(dt));
            } else {
              double nu = cell.getNumericCellValue();
              data.get(iRow).add(nu + "");
              System.out.printf("%d:%d:no=%f\t", iRow, iCol, nu);
            }
            break;

          case BOOLEAN:
            boolean boo = cell.getBooleanCellValue();
            data.get(iRow).add(boo + "");
            System.out.printf("%d:%d:b=%s\t", iRow, iCol, (boo ? "True" : "False"));
            break;

          case FORMULA:
            String form = cell.getCellFormula();
            data.get(iRow).add(form + "");
            System.out.printf("%d:%d:frm=%s\t", iRow, iCol, form);
            break;

          default:
            data.get(Integer.valueOf(iRow)).add(" ");
            System.out.printf("%d:%d:?=*\t", iRow, iCol);
            break;
        }
        iCol++;
      }
      System.out.println();
      iRow++;
    }

  }

}
