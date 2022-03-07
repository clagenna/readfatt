package prova.dataset;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.Test;

import sm.readfatt.dataset.Dataset;

public class ProvaDataSet {

  private Dataset dts;

  @Test
  public void doTheJob() {
    initialize();
    Path pth = Paths.get("dati", "PrimaFatt.txt");
    try (Stream<String> linee = Files.lines(pth, Charset.forName("Cp1252"))) {
      linee.forEach(s -> leggiRiga(s));
    } catch (IOException e) {
      e.printStackTrace();
    }
    dts.printData();
  }

  private void initialize() {
    dts = new Dataset();
    // Credito precedente anno 2020: 633 kWh
    dts.addCol("CredAnnoPrec:i:h:2");
    dts.addCol("CredKwhPrec:i:i:2");

    // Credito attuale anno 2020: 0 kWh
    dts.addCol("CredAnnoAttuale:i:h:3");
    dts.addCol("CredKwhAtt:i:i:3");

    // Corrispettivo potenza impegnata 01/05/2021 31/05/2021  0,896910  4,50  4,04�/KW
    dts.addCol("PotDtDa:d:-:-:1");
    dts.addCol("PotDtA:d:-:-");
    dts.addCol("PotCostUnit:f:h:3");
    dts.addCol("PotContatore:f:f:2");
    dts.addCol("PotTotale:f:-:-");

    // Corrispettivo energia 01/07/2021 31/07/2021  0,089450  163  14,58�/kWh1� scaglione
    dts.addCol("EneDtDa:d:-:-:1");
    dts.addCol("EneDtA:d:-:-:1");
    dts.addCol("EneCostoUnit:f:l:2");
    dts.addCol("EneQta:i:-:-:1");
    dts.addCol("EneTotale:f:-:-:1");

    // Corrispettivo energia 01/07/2021 31/07/2021  0,089450  163  14,58�/kWh2� scaglione
    dts.addCol("Ene2DtDa:d:-:-:1");
    dts.addCol("Ene2DtA:d:-:-:1");
    dts.addCol("Ene2CostoUnit:f:l:3");
    dts.addCol("Ene2Qta:i:-:-:1");
    dts.addCol("Ene2Totale:f:-:-:1");

    // Tariffa raccolta rifiuti 01/08/2021 31/08/2021  0,059130  45  2,66�/kWh
    dts.addCol("RifiutiDtDa:d:-:-:1");
    dts.addCol("RifiutiDtA:d:-:-:1");
    dts.addCol("RifiutiCostoUnit:f:n:2");
    dts.addCol("RifiutiQta:i:-:-:1");
    dts.addCol("RifiutiTotale:f:-:-:1");

    dts.addCol("FattNo:br:e:1");
    dts.addCol("DataEmiss:d:-:-");
    dts.addCol("TotPagare:cy:b:-");

    // 30/04/2021 31/05/2021Energia Attiva  25.173 24.879 LETTURA REALE  294,00  1,00
    dts.addCol("LettDtPrec:d:-:-:1");
    dts.addCol("LettDtAttuale:d:c:5:1");
    dts.addCol("LettAttuale:i:e:5:1}");
    dts.addCol("LettPrec:i:d:5:1");
    dts.addCol("LettConsumo:f:-:-:1");
    dts.addCol("LettCoeffK:f:-:-:1");

    String regx = "Servizio Energia Elettrica Fattura n. +${FattNo} +Data Emissione ${DataEmiss}.*";
    dts.addRegexRiga(regx, "Servizio Energia Elettrica");

    regx = "^${TotPagare}.*";
    dts.addRegexRiga(regx);

    regx = "Credito precedente anno ${CredAnnoPrec}: ${CredKwhPrec} +kWh";
    dts.addRegexRiga(regx, "Credito precedente");

    regx = "Credito attuale anno ${CredAnnoAttuale}: ${CredKwhAtt} kWh";
    dts.addRegexRiga(regx, "Credito attuale");

    regx = "${LettDtPrec} ${LettDtAttuale}Energia Attiva +${LettAttuale} ${LettPrec} LETTURA REALE +${LettConsumo} +${LettCoeffK}";
    dts.addRegexRiga(regx, "LETTURA REALE");

    regx = "Corrispettivo +potenza +impegnata +${PotDtDa} +${PotDtA} +${PotCostUnit} +${PotContatore} +${PotTotale}./KW";
    dts.addRegexRiga(regx, "potenza impegnata");

    regx = "Corrispettivo +energia +${EneDtDa} +${EneDtA} +${EneCostoUnit} +${EneQta} +${EneTotale}./kWh1. +scaglione";
    dts.addRegexRiga(regx, "Corrispettivo energia.*1. scaglione");
    regx = "Corrispettivo +energia +${Ene2DtDa} +${Ene2DtA} +${Ene2CostoUnit} +${Ene2Qta} +${Ene2Totale}./kWh2. +scaglione";
    dts.addRegexRiga(regx, "Corrispettivo energia.*2. scaglione");

    regx = "Tariffa +raccolta +rifiuti +${RifiutiDtDa} +${RifiutiDtA} +${RifiutiCostoUnit} +${RifiutiQta} +${RifiutiTotale}./kWh";
    dts.addRegexRiga(regx, "raccolta rifiuti");

  }

  private void leggiRiga(String p_sz) {
    if (p_sz == null || p_sz.length() < 3)
      return;
    dts.parseRiga(p_sz);
    //    for (GestRiga gr : m_li) {
    //      if (gr.parse(p_sz)) {
    //        System.out.println(p_sz);
    //        System.out.println("---------------------------------------");
    //        System.out.println(gr.toString());
    //        System.out.println("---------------------------------------");
    //        break;
    //      }
    //    }
  }

}
