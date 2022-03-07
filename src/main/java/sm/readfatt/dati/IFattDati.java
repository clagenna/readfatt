package sm.readfatt.dati;

public interface IFattDati {
  IFattDati addRegex(String p_rex);

  IFattDati addField(String p_name, ETipiDato p_tp, String prow, String pcol);

  boolean parse(String p_sz);
}
