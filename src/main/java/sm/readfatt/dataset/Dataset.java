package sm.readfatt.dataset;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import sm.readfatt.dati.ETipiDato;

public class Dataset {

  @Getter @Setter private List<DtsCol> colonne;
  private Map<String, DtsCol>          m_mapCol;
  private List<GestRiga2>              m_liGestRiga;

  @Getter @Setter private List<DtsRow> righe;
  private int                          currentRow;

  public Dataset() {
    init();
  }

  private void init() {
    colonne = new ArrayList<DtsCol>();
    m_mapCol = new HashMap<>();
    righe = new ArrayList<>();
    currentRow = 0;
  }

  public void addCol(String string) {
    DtsCol col = new DtsCol(this);
    col.parseRegex(string);
    int nIndx = colonne.size();
    col.setColIndex(nIndx);
    colonne.add(col);
    m_mapCol.put(col.getName(), col);
  }

  public void addRegexRiga(String regx) {
    addRegexRiga(regx, null);
  }

  /**
   * All'interno della espressione regolare che guida l'interpretazione della
   * riga col metodo {@link #parse(String)} metto dei tag
   * <code><i>${...}</i></code> per identificare i campi che saranno
   * estrapolati<br/>
   * Ogni tag contiene:
   * <ol>
   * <li>il nome del campo</li>
   * <li>la tipologia di campo dal enumerato {@link ETipiDato}</li>
   * <li>la riga Excel di destinazione</li>
   * <li>la colonna Excel di destinazione</li>
   * </ol>
   * il metodo interpreta i vari tag e li aggiunge con
   * {@link #addField(String, ETipiDato)}
   *
   * @param p_rex
   */
  public void addRegexRiga(String p_rex, String pCivetta) {
    if (m_liGestRiga == null)
      m_liGestRiga = new ArrayList<GestRiga2>();
    GestRiga2 gr = new GestRiga2(this);
    gr.parseRegex(p_rex, pCivetta);
    m_liGestRiga.add(gr);
  }

  public DtsCol getColonna(String pnome) {
    DtsCol col = m_mapCol.get(pnome);
    return col;
  }

  public void parseRiga(String p_sz) {
    for (GestRiga2 gr : m_liGestRiga) {
      if (gr.parseRiga(p_sz.replace("\r", "")))
        break;
    }

  }

  public DtsData addData(DtsData pdata) {
    DtsRow riga = null;
    boolean bNewRow = pdata.getColonna().isMultiRow();
    if (righe.size() == 0) {
      riga = new DtsRow(this);
      righe.add(riga);
    }
    // con newrow scrivo (eventualmente) su nuova riga
    // altrimenti scrivo sempre sulla zero
    if (bNewRow) {
      riga = righe.get(currentRow);
      if ( !riga.isLibero(pdata)) {
        riga = new DtsRow(this);
        righe.add(riga);
        currentRow++;
      }
    } else
      riga = righe.get(0);
    riga.addData(pdata);
    return pdata;
  }

  public int getColIndex(DtsCol colonna) {
    return colonna.getColIndex();
  }

  public void printData() {
    SimpleDateFormat dtfmt = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String sz = dtfmt.format(new Date());
    String szFilNam = String.format("dati/Dataset_out_%s.txt", sz);
    try (PrintWriter pwr = new PrintWriter(new File(szFilNam))) {
      if (righe == null) {
        pwr.println("** no data **");
        return;
      }
      String virg = "";
      for (DtsCol co : colonne) {
        pwr.print(virg + co.getName());
        virg = ";";
      }
      pwr.println();
      // int k=0;
      for (DtsRow ri : righe) {
        virg = "";
        // pwr.println( (k++) + ")" + ri);
        for (DtsData da : ri.getDati()) {
          sz = "";
          if (da != null) {
            Object obj = da.getDato();
            if (obj instanceof Date)
              sz = DtsData.s_dtfmt.format((Date) obj);
            else
              sz = String.valueOf(da.getDato());
          }
          pwr.print(virg + sz);
          virg = ";";
        }
        pwr.println();
      }
      System.out.println("Scritto file:" + szFilNam);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * se fornisco i<0 allora torno l'ultima riga
   * 
   * @param string
   * @param i
   * @return
   */
  public Object getValue(String string, int i) {
    int k = i;
    if (k < 0)
      k = righe.size() - 1;
    if (k > righe.size() - 1)
      throw new ArrayIndexOutOfBoundsException(k);
    DtsRow riga = righe.get(k);
    return riga.getValue(string);
  }

}
