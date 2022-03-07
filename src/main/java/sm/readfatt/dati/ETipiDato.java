package sm.readfatt.dati;

import java.util.HashMap;
import java.util.Map;

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
    for (ETipiDato tp : values()) {
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
