package prova.readfatt;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;

import sm.readfatt.dati.GestRiga;

public class ProvaTransTag {

  private List<GestRiga> m_li;

  @Test
  public void doTheJob() {
    initialize();
    Path pth = Paths.get("dati", "PrimaFatt.txt");
    try (Stream<String> linee = Files.lines(pth, Charset.forName("Cp1252"))) {
      linee.forEach(s -> leggiRiga(s));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void initialize() {
    m_li = new ArrayList<GestRiga>();
    GestRiga gr = new GestRiga("Servizio Energia Elettrica");
    String regx = "Servizio Energia Elettrica Fattura n. +${FattNo:br:e:1} +Data Emissione ${DataEmiss:d:-:-}.*";
    gr.addRegex(regx);
    m_li.add(gr);

    gr = new GestRiga();
    regx = "^${TotPagare:cy:b:2}.*";
    gr.addRegex(regx);
    m_li.add(gr);

    gr = new GestRiga("LETTURA REALE");
    regx = "${LettDtPrec:d:-:-:1} ${LettDtAttuale:d:c:5:1}Energia Attiva +${LettAttuale:i:e:5:1} ${LettPrec:i:d:5:1} LETTURA REALE +${LettConsumo:f:-:-:1} +${LettCoeffK:f:-:-:1}";
    gr.addRegex(regx);
    m_li.add(gr);

  }

  private void leggiRiga(String p_sz) {
    if (p_sz == null || p_sz.length() < 3)
      return;
//    System.out.println(p_sz);
//    if (p_sz.contains("LETTURA REALE"))
//      System.out.println("trovato !");
    for (GestRiga gr : m_li) {
      if (gr.parse(p_sz)) {
        System.out.println(p_sz);
        System.out.println("---------------------------------------");
        System.out.println(gr.toString());
        System.out.println("---------------------------------------");
        break;
      }
    }
  }

}
