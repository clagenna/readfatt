package prova.xlsx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Prova02DupSheet {
  private static final String CSZ_SRC  = "dati/Fattura_Templ.xlsx";
  private static final String CSZ_DST  = "dati/Fattura_Dupl.xlsx";
  private static final String CSZ_NOME = "Oggi";

  private XSSFWorkbook        dstwkb;

  public static void main(String[] args) {

    Prova02DupSheet app = new Prova02DupSheet();
    app.doTheJob2(CSZ_SRC, CSZ_DST);
  }

  private void doTheJob2(String xlsSrc, String xlsDst) {
    XSSFWorkbook wkb = null;
    try (InputStream fiin = new FileInputStream(new File(xlsSrc))) {
      wkb = new XSSFWorkbook(fiin);
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      Sheet srcsh = wkb.getSheetAt(0);
      dstwkb = new XSSFWorkbook();
      XSSFSheet dstsh = dstwkb.createSheet(CSZ_NOME);
      copyWorkb(srcsh, dstsh);
      FileOutputStream fou = new FileOutputStream(new File(xlsDst));
      dstwkb.write(fou);
      dstwkb.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private void copyWorkb(Sheet srcsh, XSSFSheet dstsh) {
    Map<XSSFCellStyle, XSSFCellStyle> mapSty = new HashMap<>();
    String sz = null;
    int riga = 0;
    int col = 0;
    for (Row srcrow : srcsh) {
      riga = srcrow.getRowNum();
      Row dstrow = dstsh.createRow(riga);
      for (Cell srccel : srcrow) {
        col = srccel.getColumnIndex();
        Cell dstcell = dstrow.createCell(col);
        switch (srccel.getCellType()) {

          case BLANK:
          case _NONE:
            dstcell.setCellValue("");
            break;

          case BOOLEAN:
            boolean boo = srccel.getBooleanCellValue();
            dstcell.setCellValue(Boolean.valueOf(boo));
            break;

          case ERROR:
            dstcell.setCellValue("*err*");
            break;

          case FORMULA:
            sz = srccel.getCellFormula();
            dstcell.setCellFormula(sz);
            break;

          case NUMERIC:
            if (DateUtil.isCellDateFormatted(srccel)) {
              Date dt = srccel.getDateCellValue();
              dstcell.setCellValue(dt);
            } else {
              double dbl = srccel.getNumericCellValue();
              dstcell.setCellValue(dbl);
            }
            break;

          case STRING:
            sz = srccel.getStringCellValue();
            dstcell.setCellValue(sz);
            break;

          default:
            System.err.println("case default ?!?");
            break;

        }
        XSSFCellStyle srcsty = (XSSFCellStyle) srccel.getCellStyle();
        XSSFCellStyle dststy = mapSty.get(srcsty);
        // se non ho gia il clone dello stile lo creo
        if (dststy == null) {
          dststy = dstwkb.createCellStyle();
          dststy.cloneStyleFrom(srcsty);
          dststy.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
          mapSty.put(srcsty, dststy);
        }
        if (dststy != null)
          dstcell.setCellStyle(dststy);
      }
    }

  }
}
