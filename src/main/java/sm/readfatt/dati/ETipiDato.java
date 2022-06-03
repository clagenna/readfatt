package sm.readfatt.dati;

import java.util.HashMap;
import java.util.Map;

import sm.readfatt.dataset.Dataset;
import sm.readfatt.dataset.DtsCol;

/**
 * Enumerato dei tipi di dato che si possono incontrare nelle fatture. Ogni tipo
 * di dato ha la sua RegEx di identificazione. Questa RegEx è utilizzata nel
 * {@link Dataset} su ogni {@link DtsCol} per <i>parse-are<i> il valore.
 *
 * @author claudio
 *
 */
public enum ETipiDato {
  Intero("i", "([0-9][0-9.]*)"), //
  Float("f", "(\\d+\\,\\d+)"), //
  Importo("cy", "([0-9,]+) €"), //
  Barrato("br", "(\\d+/\\d+)"), //
  Stringa("s", "([a-zA-Z]+)"), //
  Data("d", "(\\d{2}/\\d{2}/\\d{4})");

  private String                        cod;
  private String                        regex;
  private static Map<String, ETipiDato> map;

  static {
    map = new HashMap<String, ETipiDato>();
    for (ETipiDato tp : ETipiDato.values()) {
      map.put(tp.cod, tp);
    }
  }

  private ETipiDato(String p_cod, String p_rex) {
    cod = p_cod;
    regex = p_rex;
  }

  public String getCod() {
    return cod;
  }

  public String getRegex() {
    return regex;
  }

  public static ETipiDato decode(String pcod) {
    return map.get(pcod);
  }

}
