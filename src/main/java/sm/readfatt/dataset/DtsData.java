// Generated by delombok at Mon Jun 13 17:57:30 CEST 2022
package sm.readfatt.dataset;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

import sm.readfatt.sys.Utils;

public class DtsData {
  private static NumberFormat s_cyfmt = NumberFormat.getCurrencyInstance();
  private DtsCol              colonna;
  private Object              dato;

  public DtsData(DtsCol pcol) {
    setColonna(pcol);
  }

  public Object parse(String p_sz) {
    String sz = null;
    dato = null;
    try {
      switch (getColonna().getTipoDato()) {
        case Data:
          dato = Utils.s_fmtDMY4.parse(p_sz);
          break;
        case Float:
          sz = p_sz.replace(",", ".");
          dato = Float.parseFloat(sz);
          break;
        case Importo:
          sz = p_sz.replace(",", ".");
          dato = new BigDecimal(sz);
          break;
        case Intero:
          sz = p_sz.replace(".", "");
          dato = Integer.valueOf(sz);
          break;
        case Stringa:
          dato = p_sz;
          break;
        case Barrato:
          dato = p_sz;
          break;
      }
    } catch (NumberFormatException | ParseException e) {
      e.printStackTrace();
    }
    return dato;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    String myName = getColonna().getName();
    if ( ! (obj instanceof DtsData))
      return false;
    DtsData altro = (DtsData) obj;
    return myName.equals(altro.getColonna().getName());
  }

  @Override
  public String toString() {
    String sz = "*NULL*";
    if (dato != null) {
      switch (getColonna().getTipoDato()) {
        case Data:
          sz = Utils.s_fmtDMY4.format(dato);
          break;
        case Float:
          sz = String.format("%.2f", dato);
          break;
        case Importo:
          sz = s_cyfmt.format(dato);
          break;
        case Intero:
          sz = dato.toString();
          break;
        case Stringa:
          sz = (String) dato;
          break;
        case Barrato:
          sz = (String) dato;
          break;
        default:
          break;
      }
    }
    String szRet = String.format("%s=\"%s\"", getColonna().getName(), sz);
    return szRet;
  }

  public String getName() {
    String l_sz = "*";
    DtsCol col  = getColonna();
    if (col == null || col.getName() == null)
      return l_sz;
    l_sz = col.getName();
    return l_sz;
  }

  public DtsCol getColonna() {
    return this.colonna;
  }

  public void setColonna(final DtsCol colonna) {
    this.colonna = colonna;
  }

  public Object getDato() {
    return this.dato;
  }

  public void setDato(final Object dato) {
    this.dato = dato;
  }
}
