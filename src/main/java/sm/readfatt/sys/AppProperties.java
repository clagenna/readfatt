package sm.readfatt.sys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import lombok.Getter;
import lombok.Setter;
import sm.readfatt.sys.ex.ReadFattPropsException;

public class AppProperties {
  private static AppProperties s_inst;

  private Properties           properties;
  @Getter @Setter private File propertyFile;

  /**
   * codoe X email di servizio, non pertinenti ad un OE specifico ma prettamente
   * applicativo. Numero fittizio di OE *non* ESISTENTE
   */

  public static final String   PROP_BASE_DIR = "app.basedir";

  public AppProperties() throws ReadFattPropsException {
    if (s_inst == null)
      s_inst = this;
    else {
      throw new ReadFattPropsException("App Prop. gia istanziato !");
    }
  }

  public static AppProperties getInstance() {
    return s_inst;
  }

  public Properties getProperties() {
    if (properties == null)
      properties = new Properties();
    return properties;
  }

  /**
   * @see #getPropertyFile()
   */
  public void setPropertyFile(File p_fiProp) {
    if (Utils.isChanged(p_fiProp, propertyFile)) {
      propertyFile = p_fiProp;
    }
  }

  public Properties leggiPropertyFile() throws ReadFattPropsException {
    return leggiPropertyFile(getPropertyFile());
  }

  public Properties leggiPropertyFile(String p_fiProp) throws ReadFattPropsException {
    return leggiPropertyFile(new File(p_fiProp));
  }

  public Properties leggiPropertyFile(File p_fiProp) throws ReadFattPropsException {
    if (p_fiProp == null || !p_fiProp.exists()) {
      throw new ReadFattPropsException(
          "Il file properties non esiste:" + (p_fiProp != null ? p_fiProp.getAbsolutePath() : "*NULL*"));
    }
    System.out.println("Apro il file di  properties:" + p_fiProp.getAbsolutePath());
    setPropertyFile(p_fiProp);
    try (InputStream is = new FileInputStream(p_fiProp)) {
      properties = new Properties();
      properties.load(is);
      setPropertyFile(p_fiProp);
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Errore apertura property file:" + p_fiProp.getAbsolutePath() + " " + e);
    }
    return properties;
  }

  public void salvaSuProperties(File p_fiProp) {
    setPropertyFile(p_fiProp);
    try (FileOutputStream fos = new FileOutputStream(getPropertyFile())) {
      properties.store(fos, Utils.s_fmtY4MDHMS.format(new Date()));
      System.out.println("salvato properties sul file:" + getPropertyFile().getAbsolutePath());
    } catch (IOException e) {
      System.err.println("Errore in salvataggio delle properties:" + getPropertyFile().getAbsolutePath() + " " + e);
    }
  }

  public String getProperty(String p_propName) {
    String szValue = null;
    Object obj = getProperties().get(p_propName);
    if (obj != null)
      szValue = obj.toString().trim();
    else
      System.err.printf("La property %s torna valore *NULL*\n", p_propName);
    return szValue;
  }

  public void setProperty(String p_propName, String p_value) {
    getProperties();
    if (Utils.isValue(p_value))
      properties.setProperty(p_propName, p_value);
    else
      properties.remove(p_propName);
  }

  public int getIntProperty(String p_propName, int p_defVal) {
    int ret = getIntProperty(p_propName);
    if (ret == -1)
      ret = p_defVal;
    return ret;
  }

  public int getIntProperty(String p_propName) {
    int nRet = -1;
    String sz = getProperty(p_propName);
    if (Utils.isNumeric(sz))
      nRet = Integer.parseInt(sz);
    return nRet;
  }

  public boolean getBooleanProperty(String p_prop) {
    boolean bRet = false;
    String sz = getProperty(p_prop);
    if (sz != null) {
      switch (sz) {
        case "0":
        case "n":
        case "no":
          bRet = false;
          break;
        case "-1":
        case "1":
        case "s":
        case "y":
        case "si":
        case "yes":
          bRet = true;
          break;
        default:
          bRet = Boolean.parseBoolean(sz);
          break;
      }
    }
    return bRet;
  }

}