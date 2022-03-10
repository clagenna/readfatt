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

import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
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

import sm.readfatt.dataset.Dataset;
import sm.readfatt.dataset.DtsCol;
import sm.readfatt.dataset.DtsRow;
import sm.readfatt.sys.AppProperties;
import sm.readfatt.sys.ParseCmdLine;
import sm.readfatt.sys.Utils;
import sm.readfatt.sys.ex.ReadFattException;
import sm.readfatt.sys.ex.ReadFattPDFException;

public class MainApp {
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
      e.printStackTrace();
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
    // m_dts = creaDataset();
    m_dts = new Dataset();
    m_dts.readPropertyFields();
    analizzaFilePDF();
    m_dts.printData();
    copiaXlsxTempl();
    copiaDatiInXlsx();
    salvaXlsx();
    lanciaExcel();
  }

  private String convertPDFDocument(File fi) throws ReadFattPDFException {
    String text;
    try {
      PDFTextStripper stripper = new PDFTextStripper();
      RandomAccessRead rndacc = new RandomAccessBufferedFileInputStream(fi);
      PDFParser parser = new PDFParser(rndacc);
      parser.parse();
      PDDocument doc = parser.getPDDocument();
      text = stripper.getText(doc);
      doc.close();
    } catch (IOException e) {
      throw new ReadFattPDFException(String.format("Err. convers.", fi.getAbsolutePath()));
    }
    return text;
  }

  private Dataset creaDataset() {
    Dataset dts = new Dataset();
    // Credito precedente anno 2020: 633 kWh
    dts.addCol("CredAnnoPrec:i:h:2");
    dts.addCol("CredKwhPrec:i:i:2");

    // Credito attuale anno 2020: 0 kWh
    dts.addCol("CredAnnoAttuale:i:h:3");
    dts.addCol("CredKwhAtt:i:i:3");

    // Corrispettivo potenza impegnata 01/05/2021 31/05/2021  0,896910  4,50  4,04�/KW
    dts.addCol("PotDtDa:d:-:-");
    dts.addCol("PotDtA:d:-:-");
    dts.addCol("PotCostUnit:f:-:-");
    dts.addCol("PotContatore:f:f:2");
    dts.addCol("PotTotale:f:-:-");

    // Corrispettivo energia 01/07/2021 31/07/2021  0,089450  163  14,58�/kWh1� scaglione
    dts.addCol("EneDtDa:d:-:-:1");
    dts.addCol("EneDtA:d:-:-:1");
    dts.addCol("EneCostoUnit:f:l:2");
    dts.addCol("EneQta:i:-:-:1");
    dts.addCol("EneTotale:f:-:-:1");

    // Corrispettivo energia 01/07/2021 31/07/2021  0,089450  163  14,58�/kWh2� scaglione
    dts.addCol("Ene2DtDa:d:-:-:1");
    dts.addCol("Ene2DtA:d:-:-:1");
    dts.addCol("Ene2CostoUnit:f:l:3");
    dts.addCol("Ene2Qta:i:-:-:1");
    dts.addCol("Ene2Totale:f:-:-:1");

    // Tariffa raccolta rifiuti 01/08/2021 31/08/2021  0,059130  45  2,66�/kWh
    dts.addCol("RifiutiDtDa:d:-:-:1");
    dts.addCol("RifiutiDtA:d:-:-:1");
    dts.addCol("RifiutiCostoUnit:f:n:1");
    dts.addCol("RifiutiQta:i:-:-:1");
    dts.addCol("RifiutiTotale:f:-:-:1");

    dts.addCol("FattNo:br:e:1");
    dts.addCol("DataEmiss:d:-:-");
    dts.addCol("TotPagare:cy:b:2");

    // 30/04/2021 31/05/2021Energia Attiva  25.173 24.879 LETTURA REALE  294,00  1,00
    dts.addCol("LettDtPrec:d:-:-:1");
    dts.addCol("LettDtAttuale:d:c:5:1");
    dts.addCol("LettAttuale:i:e:5:1}");
    dts.addCol("LettPrec:i:d:5:1");
    dts.addCol("LettConsumo:f:-:-:1");
    dts.addCol("LettCoeffK:f:-:-:1");

    String regx = "Servizio Energia Elettrica Fattura n. +${FattNo} +Data Emissione ${DataEmiss}.*";
    dts.addRegexRiga(regx, "Servizio Energia Elettrica");

    regx = "^${TotPagare}.*";
    dts.addRegexRiga(regx);

    regx = "Credito precedente anno ${CredAnnoPrec}: ${CredKwhPrec} +kWh";
    dts.addRegexRiga(regx, "Credito precedente");

    regx = "Credito attuale anno ${CredAnnoAttuale}: +${CredKwhAtt} +kWh";
    dts.addRegexRiga(regx, "Credito attuale");

    regx = "${LettDtPrec} ${LettDtAttuale}Energia Attiva +${LettAttuale} ${LettPrec} LETTURA REALE +${LettConsumo} +${LettCoeffK}";
    dts.addRegexRiga(regx, "LETTURA REALE");

    regx = "Corrispettivo +potenza +impegnata +${PotDtDa} +${PotDtA} +${PotCostUnit} +${PotContatore} +${PotTotale}./KW";
    dts.addRegexRiga(regx, "potenza impegnata");

    regx = "Corrispettivo +energia +${EneDtDa} +${EneDtA} +${EneCostoUnit} +${EneQta} +${EneTotale}./kWh1. +scaglione";
    dts.addRegexRiga(regx, "Corrispettivo energia.*1. scaglione");
    regx = "Corrispettivo +energia +${Ene2DtDa} +${Ene2DtA} +${Ene2CostoUnit} +${Ene2Qta} +${Ene2Totale}./kWh2. +scaglione";
    dts.addRegexRiga(regx, "Corrispettivo energia.*2. scaglione");

    regx = "Tariffa +raccolta +rifiuti +${RifiutiDtDa} +${RifiutiDtA} +${RifiutiCostoUnit} +${RifiutiQta} +${RifiutiTotale}./kWh";
    dts.addRegexRiga(regx, "raccolta rifiuti");

    return dts;
  }

  private void analizzaFilePDF() {
    Stream<String> stre = Arrays.stream(m_pdfText.split("\\n"));
    stre.forEach(s -> analizzaRiga(s));
  }

  private void analizzaRiga(String p_sz) {
    if (p_sz == null || p_sz.length() < 3)
      return;
    m_dts.parseRiga(p_sz);
  }

  private void copiaXlsxTempl() {
    Date lastDt = cercaLastDate();
    String szNomeSheet = Utils.s_fmtY4MD.format(lastDt);
    m_szXlsxFile = String.format("EE_%s.xlsx", szNomeSheet);
    Workbook srcwkb = null;
    try (InputStream fiin = new FileInputStream(new File(CSZ_XLSXSRC))) {
      srcwkb = new XSSFWorkbook(fiin);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Sheet srcsh = srcwkb.getSheetAt(0);
    m_dstwkb = new XSSFWorkbook();
    m_dstsh = m_dstwkb.createSheet(szNomeSheet);
    copyWorkb(srcsh, m_dstsh);
  }

  private void copiaDatiInXlsx() {
    int k = 0;
    String sz = null;
    for (DtsRow riga : m_dts.getRighe()) {
      for (DtsCol col : m_dts.getColonne()) {
        Object val = riga.getValue(col.getName());
        if (val == null)
          continue;
        int exlRow = parseExcelRow(col.getExcelrow());
        int exlCol = parseExcelCol(col.getExcelcol());
        boolean bMultiRow = col.isMultiRow();
        if ( (exlCol * exlRow == 0) || ( !bMultiRow && k > 0))
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
        //        switch (dstcell.getCellType()) {
        //
        //          case BLANK:
        //          case _NONE:
        //            dstcell.setCellValue(String.valueOf(val));
        //            break;
        //
        //          case BOOLEAN:
        //            boolean boo = Boolean.parseBoolean(val.toString());
        //            dstcell.setCellValue(Boolean.valueOf(boo));
        //            break;
        //
        //          case ERROR:
        //            dstcell.setCellValue("*err*");
        //            break;
        //
        //          case FORMULA:
        //            System.err.printf("formula in cella (%d,%d)\n", exlRow, exlCol);
        //            break;
        //
        //          case NUMERIC:
        //            if (DateUtil.isCellDateFormatted(dstcell)) {
        //              Date dt = (Date) val;
        //              dstcell.setCellValue(dt);
        //            } else {
        //              double dbl = Double.parseDouble(val.toString());
        //              dstcell.setCellValue(dbl);
        //            }
        //            break;
        //
        //          case STRING:
        //            sz = val.toString();
        //            dstcell.setCellValue(sz);
        //            break;
        //
        //          default:
        //            System.err.println("case default ?!?");
        //            break;
        //
        //        }
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
            break;
        }
      }
      k++;
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
      e.printStackTrace();
    }

  }

  private void lanciaExcel() {
    try {
      File fi = new File(m_szXlsxFile);
      String sz = fi.getAbsolutePath();
      String szCmd = String.format("cmd /c start excel.exe \"%s\"", sz);
      Runtime.getRuntime().exec(szCmd);
    } catch (IOException e) {
      e.printStackTrace();
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
