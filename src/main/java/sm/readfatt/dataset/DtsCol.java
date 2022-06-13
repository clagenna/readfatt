// Generated by delombok at Mon Jun 13 17:57:30 CEST 2022
package sm.readfatt.dataset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sm.readfatt.dati.ETipiDato;

/**
 * Nel {@link Dataset} rappresenta la descrizione della singola colonna ivi
 * presente
 *
 * @author claudio
 */
public class DtsCol {
  @java.lang.SuppressWarnings("all")
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(DtsCol.class);
  private static final Logger s_log = LogManager.getLogger(DtsCol.class);
  @SuppressWarnings("unused")
  private Dataset m_dataset;
  private String name;
  private ETipiDato tipoDato;
  private String excelrow;
  private String excelcol;
  private String seMultiRow;
  private int colIndex;
  private boolean buono;

  public DtsCol(Dataset dataset) {
    m_dataset = dataset;
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
   * @param p_fld
   */
  public void parseRegex(String p_fld) {
    String[] arr = p_fld.split(":");
    if (arr.length < 4) {
      s_log.error("il tag \"{}\" non ha campi a sufficienza !!", p_fld);
      return;
    }
    String nome = arr[0];
    String tipo = arr[1];
    String excCol = arr[2];
    String excRow = arr[3];
    String seArr = "0";
    if (arr.length > 4) seArr = arr[4];
    ETipiDato tp = ETipiDato.decode(tipo);
    if (excCol != null && excCol.equals("-")) if (excRow == null || !excRow.equals("-")) s_log.error("Incongruenza Excel coord per {} con coord col {} rig {}", nome, excCol, excRow);
    setName(nome);
    setTipoDato(tp);
    setExcelrow(excRow);
    setExcelcol(excCol);
    setSeMultiRow(seArr);
  }

  public boolean isMultiRow() {
    if (seMultiRow == null || seMultiRow.length() == 0 || seMultiRow.equals("0")) return false;
    return true;
  }

  @Override
  public String toString() {
    String sz = String.format("%s[%s,%s]" //
    , (name == null ? "*null*" : name) //
    , (tipoDato == null ? "*null*" : tipoDato.toString()) //
    , (isMultiRow() ? "+" : " ") //
    );
    return sz;
  }

  @java.lang.SuppressWarnings("all")
  public String getName() {
    return this.name;
  }

  @java.lang.SuppressWarnings("all")
  public void setName(final String name) {
    this.name = name;
  }

  @java.lang.SuppressWarnings("all")
  public ETipiDato getTipoDato() {
    return this.tipoDato;
  }

  @java.lang.SuppressWarnings("all")
  public void setTipoDato(final ETipiDato tipoDato) {
    this.tipoDato = tipoDato;
  }

  @java.lang.SuppressWarnings("all")
  public String getExcelrow() {
    return this.excelrow;
  }

  @java.lang.SuppressWarnings("all")
  public void setExcelrow(final String excelrow) {
    this.excelrow = excelrow;
  }

  @java.lang.SuppressWarnings("all")
  public String getExcelcol() {
    return this.excelcol;
  }

  @java.lang.SuppressWarnings("all")
  public void setExcelcol(final String excelcol) {
    this.excelcol = excelcol;
  }

  @java.lang.SuppressWarnings("all")
  public String getSeMultiRow() {
    return this.seMultiRow;
  }

  @java.lang.SuppressWarnings("all")
  public void setSeMultiRow(final String seMultiRow) {
    this.seMultiRow = seMultiRow;
  }

  @java.lang.SuppressWarnings("all")
  public int getColIndex() {
    return this.colIndex;
  }

  @java.lang.SuppressWarnings("all")
  public void setColIndex(final int colIndex) {
    this.colIndex = colIndex;
  }

  @java.lang.SuppressWarnings("all")
  public boolean isBuono() {
    return this.buono;
  }

  @java.lang.SuppressWarnings("all")
  public void setBuono(final boolean buono) {
    this.buono = buono;
  }
}
