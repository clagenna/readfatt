package prova.readfatt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import sm.readfatt.dati.ETipiDato;

public class ProvaRegex {
  private static final String s_primo = "qualsiasi${primo:i} eppoi ${secondo:s} vada per il resto";

  @Test
  public void provalo() {
    Pattern pat = Pattern.compile(".*\\$\\{([A-Za-z][A-Za-z0-9]+):([A-Za-z]+)\\}.*");
    String sz = s_primo;
    while (true) {
      Matcher mtch = pat.matcher(sz);
      if ( ! mtch.find())
        break;
      String nome = mtch.group(1);
      String tipo = mtch.group(2);
      ETipiDato tp = ETipiDato.decode(tipo);
      String cosa = String.format("${%s:%s}", nome, tipo);
      sz = sz.replace(cosa, tp.getRegex());
      System.out.println(sz);
    }
      
    

  }
}
