package sm.readfatt.sys;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Getter;
import lombok.Setter;
import sm.readfatt.sys.ex.ReadFattCmdLineException;

public class ParseCmdLine {

  private static final Logger s_log        = LogManager.getLogger(ParseCmdLine.class);
  private static ParseCmdLine s_inst;

  public static final String  CSZ_PDFINPUT = "f";
  public static final String  CSZ_PROPERTY = "p";

  private Options             m_opt;
  @Getter
  @Setter
  private String              propertyFile;
  @Getter
  @Setter
  private String              PDFFatt;

  public ParseCmdLine() {
    ParseCmdLine.s_inst = this;
    creaOtions();
  }

  public static ParseCmdLine getInstance() {
    return ParseCmdLine.s_inst;
  }

  private void creaOtions() {
    m_opt = new Options();

    m_opt.addOption(ParseCmdLine.CSZ_PDFINPUT, true, "Il file PDF fattura ");
    m_opt.getOption(ParseCmdLine.CSZ_PDFINPUT).setRequired(true);

    m_opt.addOption(ParseCmdLine.CSZ_PROPERTY, true, "Il Property File per il tipo fattura");
    m_opt.getOption(ParseCmdLine.CSZ_PROPERTY).setRequired(true);

  }

  public void parse(String[] p_args) throws ReadFattCmdLineException {
    CommandLineParser prs = new DefaultParser();
    try {
      CommandLine cmd = prs.parse(m_opt, p_args);
      discerniCommandi(cmd);
    } catch (ParseException e) {
      s_log.error("Parse cmd error", e);
      throw new ReadFattCmdLineException("Parse cmd line", e);
    }
  }

  private void discerniCommandi(CommandLine p_cmd) throws ReadFattCmdLineException {
    if ( !p_cmd.hasOption(ParseCmdLine.CSZ_PDFINPUT))
      throw new ReadFattCmdLineException("Il nome del file fattura e' obbligatorio");
    if ( !p_cmd.hasOption(ParseCmdLine.CSZ_PROPERTY))
      throw new ReadFattCmdLineException("Il nome del file Properties e' obbligatorio");
    setPDFFatt(p_cmd.getOptionValue(ParseCmdLine.CSZ_PDFINPUT));
    setPropertyFile(p_cmd.getOptionValue(ParseCmdLine.CSZ_PROPERTY));
  }
}
