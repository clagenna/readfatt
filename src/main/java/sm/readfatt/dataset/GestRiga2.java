// Generated by delombok at Mon Jun 13 17:57:30 CEST 2022
package sm.readfatt.dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sm.readfatt.dati.ETipiDato;

/**
 * La classe ha il compito di interpretare la riga fornita dal PDF in base alle
 * properties di tipo <code>dts.regexXX</code> che è composta da:
 * <ol>
 * <li>Un pattern regex più i campi colonna</li>
 * <li>eventuale campo <i>Civetta</i> che funge da stringa di ricerca di
 * identificazione</li>
 * </ol>
 * esempio:
 *
 * <pre>
 * dts.regex05=${LettDtPrec} ${LettDtAttuale}Energia Attiva +${LettAttuale} ${LettPrec} LETTURA REALE +${LettConsumo} +${LettCoeffK}
 * dts.regex05.civ=LETTURA REALE
 * </pre>
 *
 * La riga regex verrà scissa in tanti campi {@link GestDato2} che si occuperà
 * di memorizzare nel campo giusto il valore presente nella stringa durante la
 * {@link #parseRiga(String)}
 *
 * @author claudio
 *
 */
public class GestRiga2 {

  private static final Logger log = LogManager.getLogger(GestRiga2.class);
  private Pattern             m_patt;
  private String              civetta;
  private Dataset             m_dts;
  private List<GestDato2>     m_liGestDato;

  public GestRiga2(Dataset pdts) {
    m_dts = pdts;
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
  public void parseRegex(String p_rex) {
    parseRegex(p_rex, null);
  }

  public void parseRegex(String p_rex, String pCivetta) {
    setCivetta(pCivetta);
    String szRegx = p_rex;
    // non ammetto piu' di 20 tag per ogni regexp
    int    i      = 0;
    while (szRegx.contains("${")) {
      if (i++ > 20)
        break;
      GestDato2 gd = new GestDato2(m_dts);
      szRegx = gd.parseRegex(szRegx);
      if ( !gd.isBuono())
        break;
      // la Match.find() e' greedy per cui riporta per primo l'ultimo tag
      addField(gd, true);
      log.debug("Regex:{}", szRegx);
    }
    m_patt = Pattern.compile(szRegx);
  }

  private void addField(GestDato2 gd, boolean b) {
    if (m_liGestDato == null)
      m_liGestDato = new ArrayList<GestDato2>();
    if (b)
      m_liGestDato.add(0, gd);
    else
      m_liGestDato.add(gd);
  }

  /**
   * Se la riga fornita dal PDF in input <i>match-a</i> con il pattern
   * {@link GestRiga2#m_patt} allora i singoli gruppi della RegEx saranno
   * assegnati ad ogni {@link GestDato2} della lista {@link #m_liGestDato}.<br/>
   * {@link GestDato2} in fase di <i>parsing</i> si premurerà di generare un
   * {@link DtsData} da aggiungere al {@link Dataset} sottostante (con la
   * chiamata {@link Dataset#addData(DtsData)}) il quale lo aggiungerà a sua
   * volta al {@link DtsRow} corrente
   *
   * @param p_sz
   *          la riga completa fornita dal PDF di input
   * @return true se la riga <i>match-a</i>
   */
  public boolean parseRiga(String p_sz) {
    boolean bRet = false;
    if (civetta != null)
      if ( !p_sz.matches(".*" + civetta + ".*"))
        return bRet;
    // =====  solo per debug ===========================================
    String szDebug = "Credito ";
    if (p_sz.contains(szDebug))
      log.debug("Trovato \"{}\"", p_sz);
    // =================================================================
    Matcher ma = m_patt.matcher(p_sz);
    bRet = ma.find();
    if ( !bRet) {
      if (civetta != null)
        log.error("Civetta:\"{}\" Frase:\"{}\" ma... no match \"{}\"!", civetta, p_sz, ma.pattern());
      return bRet;
    }
    int k = 1;
    for (GestDato2 d : m_liGestDato) {
      String szdat = ma.group(k++);
      @SuppressWarnings("unused")
      Object obj   = d.parse(szdat);
    }
    return bRet;
  }

  @Override
  public String toString() {
    String szRet = "*null*";
    if (m_liGestDato == null || m_liGestDato.size() == 0)
      return szRet;
    szRet = "";
    for (GestDato2 dat : m_liGestDato) {
      String lsz = dat.getColonna().toString();
      lsz += "=" + m_dts.getValue(dat.getColonna().getName(), -1);
      szRet += lsz + "\n";
    }
    return szRet;
  }

  public String getCivetta() {
    return this.civetta;
  }

  public void setCivetta(final String civetta) {
    this.civetta = civetta;
  }
}
