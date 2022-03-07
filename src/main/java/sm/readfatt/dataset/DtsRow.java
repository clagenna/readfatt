package sm.readfatt.dataset;

import lombok.Getter;
import lombok.Setter;

public class DtsRow {
  @Getter @Setter private Dataset   dataset;
  @Getter @Setter private DtsData[] dati;

  public DtsRow(Dataset pdts) {
    setDataset(pdts);
    init();
  }

  private void init() {
    dati = new DtsData[dataset.getColonne().size()];
  }

  public boolean isLibero(DtsData pdata) {
    if (dati == null)
      return true;
    int ncol = pdata.getColonna().getColIndex();
    return dati[ncol] == null;
  }

  public void addData(DtsData pdata) {
    if (dati == null)
      dati = new DtsData[dataset.getColonne().size()];
    int k = pdata.getColonna().getColIndex();
    dati[k] = pdata;
  }

  @Override
  public String toString() {
    String sz = "";
    if (dati == null)
      return sz;
    int k = 0;
    String virg = "";
    for (DtsData dt : dati) {
      String l = String.format("%s(%d)%s", virg, k++, dt == null ? "" : dt.toString());
      virg = ", ";
      sz += l;
    }
    return sz;
  }

}
