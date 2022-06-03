package sm.readfatt.dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import sm.readfatt.dati.ETipiDato;

@Log4j2
public class GestRiga2 {

  private Pattern                m_patt;
  @Getter
  @Setter private String         civetta;
  private Dataset                m_dts;
  private List<GestDato2>        m_liGestDato;

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

  public boolean parseRiga(String p_sz) {
    boolean bRet    = false;
    String  szDebug = "Credito precedente";
    if (civetta != null)
      if ( !p_sz.matches(".*" + civetta + ".*"))
        return bRet;
    Matcher ma = m_patt.matcher(p_sz);
    if (p_sz.contains(szDebug))
      log.debug("Trovato \"{}\"", p_sz);
    bRet = ma.find();
    if ( !bRet) {
      if (civetta != null)
        log.error("frase:\"{}\" ma ... \"{}\" no match !", civetta, p_sz);
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

}
