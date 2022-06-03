package sm.readfatt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import lombok.extern.log4j.Log4j2;
import sm.readfatt.dataset.Dataset;
import sm.readfatt.dataset.DtsCol;
import sm.readfatt.dataset.DtsRow;
import sm.readfatt.sys.AppProperties;
import sm.readfatt.sys.ParseCmdLine;
import sm.readfatt.sys.Utils;
import sm.readfatt.sys.ex.ReadFattException;
import sm.readfatt.sys.ex.ReadFattPDFException;

@Log4j2
public class MainApp {
  private static final Logger s_log       = LogManager.getLogger(MainApp.class);
  private static final String CSZ_XLSXSRC = "dati/Fattura_Templ.xlsx";
  private File                m_pdfFile;
  private String              m_pdfText;
  private Dataset             m_dts;
  private XSSFWorkbook        m_dstwkb;
  private String              m_szXlsxFile;
  private XSSFSheet           m_dstsh;
  private AppProperties       m_props;

  public MainApp() {
    //
  }

  public static void main(String[] args) {
    MainApp app = new MainApp();
    try {
      app.init(args);
      app.vaiColTango();
    } catch (ReadFattException e) {
      MainApp.s_log.error("Parse cmd error", e);
      // e.printStackTrace();
    }
  }

  private void init(String[] p_args) throws ReadFattException {
    ParseCmdLine cmdParse = new ParseCmdLine();
    cmdParse.parse(p_args);
    String fiProp = cmdParse.getPropertyFile();
    m_props = new AppProperties();
    m_props.leggiPropertyFile(fiProp);
    m_pdfFile = new File(cmdParse.getPDFFatt());
  }

  private void vaiColTango() throws ReadFattException {
    m_pdfText = convertPDFDocument(m_pdfFile);
    if ( !verificaSeCorretto())
      return;
    // m_dts = creaDataset();
    m_dts = new Dataset();
    m_dts.readPropertyFields();
    analizzaFilePDF();
    m_dts.printData();
    copiaXlsxTempl();
    copiaDatiInXlsx();
    cancellaRigheConZero();
    salvaXlsx();
    lanciaExcel();
  }

  private String convertPDFDocument(File fi) throws ReadFattPDFException {
    String text;
    try {
      PDFTextStripper  stripper = new PDFTextStripper();
      RandomAccessRead rndacc   = new RandomAccessBufferedFileInputStream(fi);
      PDFParser        parser   = new PDFParser(rndacc);
      parser.parse();
      PDDocument doc = parser.getPDDocument();
      text = stripper.getText(doc);
      doc.close();
    } catch (IOException e) {
      text = String.format("Err. parse PDF %s", fi.getAbsolutePath());
      MainApp.s_log.error(text, e);
      throw new ReadFattPDFException(text);
    }
    return text;
  }

  private boolean verificaSeCorretto() {
    String szIdDoc = m_props.getProperty("IdDoc");
    if (szIdDoc == null) {
      log.error("Non trovo nelle properties \"IdDcc\"");
    }
    long nRows = Arrays.stream(m_pdfText.split("\\n")) //
        .filter(s -> s.contains(szIdDoc)) //
        .count();
    return nRows >= 1;
  }

  private void analizzaFilePDF() {
    Arrays.stream(m_pdfText.split("\\n")) // 
        .forEach(s -> analizzaRiga(s));
  }

  private void analizzaRiga(String p_sz) {
    if (p_sz == null || p_sz.length() < 3)
      return;
    m_dts.parseRiga(p_sz);
  }

  private void copiaXlsxTempl() {
    Date   lastDt      = cercaLastDate();
    String szNomeSheet = Utils.s_fmtY4MD.format(lastDt);
    m_szXlsxFile = String.format("EE_%s.xlsx", szNomeSheet);
    Workbook srcwkb = null;
    try (InputStream fiin = new FileInputStream(new File(MainApp.CSZ_XLSXSRC))) {
      srcwkb = new XSSFWorkbook(fiin);
    } catch (IOException e) {
      s_log.error("Errore open Xlsx template", e);
      // e.printStackTrace();
    }
    Sheet srcsh = srcwkb.getSheetAt(0);
    m_dstwkb = new XSSFWorkbook();
    m_dstsh = m_dstwkb.createSheet(szNomeSheet);
    copyWorkb(srcsh, m_dstsh);
  }

  private void copiaDatiInXlsx() {
    int    k  = 0;
    String sz = null;
    for (DtsRow riga : m_dts.getRighe()) {
      for (DtsCol col : m_dts.getColonne()) {
        Object val = riga.getValue(col.getName());
        if (val == null)
          continue;
        int     exlRow    = parseExcelRow(col.getExcelrow());
        int     exlCol    = parseExcelCol(col.getExcelcol());
        boolean bMultiRow = col.isMultiRow();
        if ( (exlCol == 0 && exlRow == 0) || ( !bMultiRow && k > 0))
          continue;
        exlRow += k;
        // -------------------------------
        XSSFRow exRow = m_dstsh.getRow(exlRow);
        if (exRow == null) {
          exRow = m_dstsh.createRow(exlRow);
        }
        XSSFCell dstcell = exRow.getCell(exlCol);
        if (dstcell == null) {
          System.err.printf("La cella %s e' *null* !", col.toString());
          dstcell = exRow.createCell(exlCol);
        }

        switch (col.getTipoDato()) {
          case Barrato:
          case Stringa:
            sz = val.toString();
            dstcell.setCellValue(sz);
            break;

          case Data:
            Date dt = (Date) val;
            dstcell.setCellValue(dt);
            break;
          case Float:
          case Importo:
          case Intero:
            double dbl = Double.parseDouble(val.toString());
            dstcell.setCellValue(dbl);
            break;
          default:
            s_log.warn("Tipo {} della Colonna {} non considerato", col.getTipoDato(), col.toString());
            break;
        }
      }
      k++;
    }
  }

  private void cancellaRigheConZero() {
    final int nColEneAtt =0;
    final int nColDtLett  = 2;
    final int nColAttuale = 4;
    boolean bEneAtt = false;
    int nRowDel = 0;
    
    for (int nRow = 0; nRow < 20; nRow++) {
      XSSFRow row = m_dstsh.getRow(nRow);
      if (row == null)
        break;
      XSSFCell eneAtt  = row.getCell(nColEneAtt);
      if ( ! bEneAtt && eneAtt != null) {
        // DateUtil.isCellDateFormatted(
       CellType typ = eneAtt.getCellType();
       if ( typ != null && typ == CellType.STRING) {
         String sz = eneAtt.getStringCellValue();
         if ( sz != null) {
           bEneAtt = sz.compareTo("Energia attiva") == 0;
         }
       }
      }
      if ( !bEneAtt)
        continue;
      XSSFCell dtLett  = row.getCell(nColDtLett);
      if ( ! DateUtil.isCellDateFormatted(dtLett))
        continue;
      XSSFCell attuale = row.getCell(nColAttuale);
      if ( attuale == null)
        continue;
      System.out.printf("cancellaRigheConZero(dt=%s, att=%s)\n", dtLett, attuale);
      CellType typ = attuale.getCellType();
      if ( typ == CellType.NUMERIC) {
        double ii = attuale.getNumericCellValue();
        if ( ii == 0) {
          if ( ++nRowDel > 4 )
            break;
          m_dstsh.removeRow(row);
        }
      }
    }

  }

  private int parseExcelRow(String excelrow) {
    int ret = 0;
    if ( !excelrow.chars().allMatch(Character::isDigit))
      return ret;
    ret = Integer.parseInt(excelrow) - 1;
    return ret;
  }

  private int parseExcelCol(String excelcol) {
    int ret = 0;
    if ( !excelcol.chars().allMatch(Character::isAlphabetic))
      return ret;

    char[] arr = new char[excelcol.length()];
    excelcol.toLowerCase().getChars(0, excelcol.length(), arr, 0);
    for (int i = arr.length - 1; i >= 0; i--) {
      char cc = (char) (arr[i] - 'a');
      ret = ret * 26 + cc;
    }
    return ret;
  }

  private void salvaXlsx() {
    try {
      FileOutputStream fou = new FileOutputStream(new File(m_szXlsxFile));
      m_dstwkb.write(fou);
      m_dstwkb.close();
    } catch (IOException e) {
      s_log.error("Errore Salva Excel", e);
      // e.printStackTrace();
    }

  }

  private void lanciaExcel() {
    try {
      File   fi    = new File(m_szXlsxFile);
      String sz    = fi.getAbsolutePath();
      String szCmd = String.format("cmd /c start excel.exe \"%s\"", sz);
      Runtime.getRuntime().exec(szCmd);
    } catch (IOException e) {
      MainApp.s_log.error("Errore lancio Excel", e);
      // e.printStackTrace();
    }
  }

  private Date cercaLastDate() {
    Date dt = (Date) m_dts.getValue("LettDtAttuale", 0);
    if (dt == null)
      throw new UnsupportedOperationException("Non trovo l'ultima data di lettura");
    return dt;
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
          dststy = m_dstwkb.createCellStyle();
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
