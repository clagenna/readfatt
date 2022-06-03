package sm.readfatt.dataset;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import sm.readfatt.dati.ETipiDato;

/**
 * Nel {@link Dataset} rappresenta la descrizione della singola colonna ivi
 * presente
 * 
 * @author claudio
 *
 */
@Log4j2
public class DtsCol {

  @SuppressWarnings("unused") private Dataset m_dataset;
  @Getter
  @Setter private String                      name;
  @Getter
  @Setter private ETipiDato                   tipoDato;
  @Getter
  @Setter private String                      excelrow;
  @Getter
  @Setter private String                      excelcol;
  @Getter
  @Setter private String                      seMultiRow;
  @Getter
  @Setter private int                         colIndex;
  @Getter
  @Setter private boolean                     buono;

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
    String arr[] = p_fld.split(":");
    if (arr.length < 4) {
      log.error("il tag \"{}\" non ha campi a sufficienza !!", p_fld);
      return;
    }
    String nome   = arr[0];
    String tipo   = arr[1];
    String excCol = arr[2];
    String excRow = arr[3];
    String seArr  = "0";
    if (arr.length > 4)
      seArr = arr[4];
    ETipiDato tp = ETipiDato.decode(tipo);
    if (excCol != null && excCol.equals("-"))
      if (excRow == null || !excRow.equals("-"))
        log.error("Incongruenza Excel coord per {} con coord col {} rig {}", nome, excCol, excRow);
    setName(nome);
    setTipoDato(tp);
    setExcelrow(excRow);
    setExcelcol(excCol);
    setSeMultiRow(seArr);
  }

  public boolean isMultiRow() {
    if (seMultiRow == null || seMultiRow.length() == 0 || seMultiRow.equals("0"))
      return false;
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
}
