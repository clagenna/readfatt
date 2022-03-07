package sm.readfatt.dati;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;

public class GestDato {

  private static SimpleDateFormat s_dtfmt = new SimpleDateFormat("dd/MM/yyyy");
  private static NumberFormat     s_cyfmt = NumberFormat.getCurrencyInstance();

  private static Pattern          s_patTag;

  static {
    s_patTag = Pattern.compile(".*\\$\\{([A-Za-z][A-Za-z0-9:\\-]+)\\}.*");
  }

  @Getter @Setter private String    name;
  @Getter @Setter private ETipiDato tipoDato;
  @Getter @Setter private Object    dato;
  @Getter @Setter private String    row;
  @Getter @Setter private String    col;
  @Getter @Setter private String    seArray;
  @Getter @Setter private boolean   buono;

  public GestDato() {
    setBuono(false);
  }

  public GestDato(String p_nam, ETipiDato p_tp) {
    setName(p_nam);
    setTipoDato(p_tp);
    setBuono(true);
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
    if ( !mtch.find())
      return szRet;
    String szMtc = mtch.group(1);
    String arr[] = szMtc.split(":");
    if (arr.length < 4) {
      System.err.printf("il tag \"%s\" non ha campi a sufficienza !!\n", szMtc);
      return szRet;
    }
    String nome = arr[0];
    String tipo = arr[1];
    String excRow = arr[2];
    String excCol = arr[3];
    String seArr = "0";
    if (arr.length > 4)
      seArr = arr[4];
    ETipiDato tp = ETipiDato.decode(tipo);
    setName(nome);
    setTipoDato(tp);
    setRow(excRow);
    setCol(excCol);
    setSeArray(seArr);
    setBuono(true);

    // sostituisco nella stringa fornita il tag con la sua espressione regolare
    String cosa = String.format("${%s}", szMtc);
    szRet = p_rex.replace(cosa, tp.getRegex());
    // System.out.println(p_rex);
    return szRet;
  }

  public Object parse(String p_sz) {
    String sz = null;
    dato = null;
    try {
      switch (tipoDato) {
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
  public String toString() {
    String sz = "*NULL*";
    if (dato != null) {
      switch (tipoDato) {
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
    String szRet = String.format("%s=\"%s\"", name, sz);
    return szRet;
  }

}
