package sm.readfatt.dataset;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import lombok.Getter;
import lombok.Setter;

public class DtsData {

  public static SimpleDateFormat s_dtfmtrev = new SimpleDateFormat("yyyyMMdd");
  public static SimpleDateFormat s_dtfmt = new SimpleDateFormat("dd/MM/yyyy");
  private static NumberFormat     s_cyfmt = NumberFormat.getCurrencyInstance();

  @Getter @Setter private DtsCol  colonna;
  @Getter @Setter private Object  dato;

  public DtsData(DtsCol pcol) {
    setColonna(pcol);
  }

  public Object parse(String p_sz) {
    String sz = null;
    dato = null;
    try {
      switch (colonna.getTipoDato()) {
        case Data:
          dato = s_dtfmt.parse(p_sz);
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
    String myName = colonna.getName();
    if ( ! (obj instanceof DtsData))
      return false;
    DtsData altro = (DtsData) obj;
    return myName.equals(altro.getColonna().getName());
  }

  @Override
  public String toString() {
    String sz = "*NULL*";
    if (dato != null) {
      switch (colonna.getTipoDato()) {
        case Data:
          sz = s_dtfmt.format(dato);
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
    String szRet = String.format("%s=\"%s\"", colonna.getName(), sz);
    return szRet;
  }

}
