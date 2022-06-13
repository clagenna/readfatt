// Generated by delombok at Mon Jun 13 17:57:30 CEST 2022
package sm.readfatt.dataset;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sm.readfatt.dati.ETipiDato;

public class GestDato2 {
  @java.lang.SuppressWarnings("all")
  private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(GestDato2.class);
  private Dataset dataset;
  private DtsCol colonna;
  private boolean buono;
  private String civetta;
  private static Pattern s_patTag;

  static {
    s_patTag = Pattern.compile(".*\\$\\{([A-Za-z][A-Za-z0-9]+)\\}.*");
  }

  @Override
  public String toString() {
    String sz = "*gest.null*";
    if (colonna != null) sz = String.format("gest(%s) col=%s", civetta, colonna.toString());
    return sz;
  }

  public GestDato2(Dataset pdts) {
    setDataset(pdts);
    setBuono(false);
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
  public String parseRegex(String p_rex) {
    String szRet = p_rex;
    Matcher mtch = s_patTag.matcher(p_rex);
    if (!mtch.find()) return szRet;
    String nome = mtch.group(1);
    colonna = dataset.getColonna(nome);
    setBuono(colonna != null);
    if (!buono) return null;
    // sostituisco nella stringa fornita il tag con la sua espressione regolare
    String cosa = String.format("${%s}", nome);
    ETipiDato tp = colonna.getTipoDato();
    szRet = p_rex.replace(cosa, tp.getRegex());
    // System.out.println(p_rex);
    return szRet;
  }

  public DtsData parse(String p_sz) {
    DtsData data = new DtsData(colonna);
    Object obj = data.parse(p_sz);
    if (obj != null) dataset.addData(data);
     else log.error("Col({}) <- [{}]", colonna.toString(), p_sz);
    return data;
  }

  @java.lang.SuppressWarnings("all")
  public Dataset getDataset() {
    return this.dataset;
  }

  @java.lang.SuppressWarnings("all")
  public void setDataset(final Dataset dataset) {
    this.dataset = dataset;
  }

  @java.lang.SuppressWarnings("all")
  public DtsCol getColonna() {
    return this.colonna;
  }

  @java.lang.SuppressWarnings("all")
  public void setColonna(final DtsCol colonna) {
    this.colonna = colonna;
  }

  @java.lang.SuppressWarnings("all")
  public boolean isBuono() {
    return this.buono;
  }

  @java.lang.SuppressWarnings("all")
  public void setBuono(final boolean buono) {
    this.buono = buono;
  }

  @java.lang.SuppressWarnings("all")
  public String getCivetta() {
    return this.civetta;
  }

  @java.lang.SuppressWarnings("all")
  public void setCivetta(final String civetta) {
    this.civetta = civetta;
  }
}
