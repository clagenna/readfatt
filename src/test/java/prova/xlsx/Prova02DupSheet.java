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
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Prova02DupSheet {
  private static final String CSZ_SRC  = "dati/Fattura_Prova.xlsx";
  private static final String CSZ_DST  = "dati/Fattura_xxxx.xlsx";
  private static final String CSZ_NOME = "Oggi";

  private XSSFWorkbook        dstwkb;

  public static void main(String[] args) {

    Prova02DupSheet app = new Prova02DupSheet();
    app.cloneXlsx(CSZ_SRC, CSZ_DST);
  }

  private void cloneXlsx(String xlsSrc, String xlsDst) {
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
      deleteRigheVuote(dstsh);
      FileOutputStream fou = new FileOutputStream(new File(xlsDst));
      dstwkb.write(fou);
      dstwkb.close();
      lanciaExcel();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private void copyWorkb(Sheet srcsh, XSSFSheet dstsh) {
    Map<XSSFCellStyle, XSSFCellStyle> mapSty = new HashMap<>();
    String                            sz     = null;
    int                               riga   = 0;
    int                               col    = 0;
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

  private void deleteRigheVuote(XSSFSheet dstsh) {
    final int nColEneAtt = 0;
    final int nColDtLett = 2;
    final int nColPreced = 3;
    // final int nColAttuale = 4;
    boolean   bEneAtt    = false;
    int       n1RowDel   = 0;
    int       n2RowDel   = 0;
    int       nRowMax    = 20;

    for (int nRow = 0; nRow < nRowMax; nRow++) {
      XSSFRow row = dstsh.getRow(nRow);
      if (row == null)
        continue;
      XSSFCell eneAtt = row.getCell(nColEneAtt);
      if ( !bEneAtt && eneAtt != null) {
        // DateUtil.isCellDateFormatted(
        CellType typ = eneAtt.getCellType();
        if (typ != null && typ == CellType.STRING) {
          String sz = eneAtt.getStringCellValue();
          if (sz != null) {
            bEneAtt = sz.compareTo("Energia attiva") == 0;
            if (bEneAtt)
              System.out.println("Trovato Energia Attiva: " + (nRow + 1));
          }
        }
      }
      if ( !bEneAtt)
        continue;
      XSSFCell dtLett = row.getCell(nColDtLett);
      if ( !DateUtil.isCellDateFormatted(dtLett))
        continue;
      XSSFCell attuale = row.getCell(nColPreced);
      if (attuale == null)
        continue;
      System.out.printf("cancellaRigheConZero(dt=%s, att=%s)\n", dtLett, attuale);
      CellType typ     = attuale.getCellType();
      boolean  bDelete = false;
      switch (typ) {
        case NUMERIC:
          double ii = attuale.getNumericCellValue();
          bDelete = ii == 0;
          break;
        case STRING:
          String sz = attuale.getStringCellValue();
          bDelete = sz == null || sz.length() == 0;
          break;
        default:
          break;
      }
      if (bDelete) {
        if (n1RowDel == 0)
          n1RowDel = nRow;
        n2RowDel = nRow;
        System.out.printf("Cancello la riga %s dal foglio\n", nRow);
        dstsh.removeRow(dstsh.getRow(nRow));
      }
    }
    if (n1RowDel > 0) {
      int lastRow = dstsh.getLastRowNum();
      System.out.printf("Shift dalla %s alla %s\n", n1RowDel, n2RowDel);
      dstsh.shiftRows(n1RowDel + 1, lastRow, -1);
    }
  }

  private void lanciaExcel() {
    try {
      File   fi    = new File(CSZ_DST);
      String sz    = fi.getAbsolutePath();
      String szCmd = String.format("cmd /c start excel.exe \"%s\"", sz);
      Runtime.getRuntime().exec(szCmd);
    } catch (IOException e) {
      System.err.printf("Errore lancio Excel:" + e.getMessage());
    }
  }

}
