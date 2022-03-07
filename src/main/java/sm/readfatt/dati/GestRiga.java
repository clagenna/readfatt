package sm.readfatt.dati;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GestRiga implements IFattDati, Cloneable {

  private String         m_szCivetta;
  private List<GestDato> m_liDato;
  private Pattern        m_patt;

  public GestRiga() {
    m_szCivetta = null;
  }

  public GestRiga(String p_civ) {
    m_szCivetta = p_civ;
  }

  @Override
  public IFattDati addRegex(String p_rex) {
    if (p_rex.contains("${")) {
      parseRegex(p_rex);
      return this;
    }
    m_patt = Pattern.compile(p_rex);
    return this;
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
  private void parseRegex(String p_rex) {
    String szRegx = p_rex;
    // non ammetto piu' di 20 tag per ogni regexp
    int i = 0;
    while (szRegx.contains("${")) {
      if (i++ > 20)
        break;
      GestDato gd = new GestDato();
      szRegx = gd.parseRegex(szRegx);
      if ( !gd.isBuono())
        break;
      // la Match.find() e' greedy per cui riporta per primo l'ultimo tag
      addField(gd, true);
      System.out.println(szRegx);
    }
    m_patt = Pattern.compile(szRegx);
  }

  public IFattDati addField(GestDato pgd, boolean bFront) {
    if (m_liDato == null)
      m_liDato = new ArrayList<GestDato>();
    if (bFront)
      m_liDato.add(0, pgd);
    else
      m_liDato.add(pgd);
    return this;
  }

  public IFattDati addField(String p_name, ETipiDato p_tp, String prow, String pcol, boolean bFront) {
    if (m_liDato == null)
      m_liDato = new ArrayList<GestDato>();
    GestDato p = new GestDato(p_name, p_tp);
    if (bFront)
      m_liDato.add(0, p);
    else
      m_liDato.add(p);
    return this;
  }

  @Override
  public IFattDati addField(String p_name, ETipiDato p_tp, String prow, String pcol) {
    return addField(p_name, p_tp, prow, pcol, false);
  }

  @Override
  public boolean parse(String p_sz) {
    boolean bRet = false;
    if (m_szCivetta != null)
      if ( !p_sz.contains(m_szCivetta))
        return bRet;
    Matcher ma = m_patt.matcher(p_sz);
    bRet = ma.find();
    if ( !bRet) {
      if (m_szCivetta != null)
        System.err.printf("frase:\"%s\" ma ... \"%s\" no match !\n", m_szCivetta, p_sz);
      return bRet;
    }
    int k = 1;
    for (GestDato d : m_liDato) {
      String szdat = ma.group(k++);
      @SuppressWarnings("unused") Object obj = d.parse(szdat);
    }
    return bRet;
  }

  public List<GestDato> getDati() {
    return m_liDato;
  }

  @Override
  public String toString() {
    String sz = "";
    for (GestDato dt : m_liDato) {
      if (sz.length() > 2)
        sz += "\n";
      sz += dt.toString();
    }
    return sz;
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {

    return super.clone();
  }

}
