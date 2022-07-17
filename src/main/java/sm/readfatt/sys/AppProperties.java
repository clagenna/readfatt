// Generated by delombok at Mon Jun 13 17:57:30 CEST 2022
package sm.readfatt.sys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sm.readfatt.sys.ex.ReadFattPropsException;

public class AppProperties {
  private static final Logger  s_log         = LogManager.getLogger(AppProperties.class);
  private static AppProperties s_inst;
  private Properties           properties;
  private File                 propertyFile;
  /**
   * codoe X email di servizio, non pertinenti ad un OE specifico ma prettamente
   * applicativo. Numero fittizio di OE *non* ESISTENTE
   */
  public static final String   PROP_BASE_DIR = "app.basedir";

  public AppProperties() throws ReadFattPropsException {
    if (AppProperties.s_inst == null)
      AppProperties.s_inst = this;
    else {
      throw new ReadFattPropsException("App Prop. gia istanziato !");
    }
  }

  public static AppProperties getInstance() {
    return AppProperties.s_inst;
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
    // System.out.println("Apro il file di  properties:" + p_fiProp.getAbsolutePath());
    s_log.info("Apro il file di  properties: {}", p_fiProp.getAbsolutePath());
    setPropertyFile(p_fiProp);
    try (InputStream is = new FileInputStream(p_fiProp)) {
      properties = new Properties();
      properties.load(is);
      setPropertyFile(p_fiProp);
    } catch (IOException e) {
      s_log.error("Errore apertura property file {}", p_fiProp.getAbsolutePath(), e);
    }
    // e.printStackTrace();
    // System.err.println("Errore apertura property file:" + p_fiProp.getAbsolutePath() + " " + e);
    return properties;
  }

  public void salvaSuProperties(File p_fiProp) {
    setPropertyFile(p_fiProp);
    try (FileOutputStream fos = new FileOutputStream(getPropertyFile())) {
      properties.store(fos, Utils.s_fmtY4MDHMS.format(new Date()));
      s_log.info("salvato properties sul file:{}", getPropertyFile().getAbsolutePath());
    } catch (IOException e) {
      s_log.error("Errore in salvataggio delle properties:{} err={}", getPropertyFile().getAbsolutePath(), e.getMessage());
    }
  }

  public String getProperty(String p_propName) {
    String szValue = null;
    Object obj     = getProperties().get(p_propName);
    if (obj != null)
      szValue = obj.toString().trim();
    else
      s_log.trace("La property {} torna valore *NULL*", p_propName);
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
    int    nRet = -1;
    String sz   = getProperty(p_propName);
    if (Utils.isNumeric(sz))
      nRet = Integer.parseInt(sz);
    return nRet;
  }

  public boolean getBooleanProperty(String p_prop) {
    boolean bRet = false;
    String  sz   = getProperty(p_prop);
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

  
  public File getPropertyFile() {
    return this.propertyFile;
  }
}
