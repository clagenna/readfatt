package prova;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

public class ProvaListFiles {

  @Test
  public void elencaFiles() {
    String    szDir = "F:\\Google Drive\\SMichele\\AASS";
    Set<Path> ele   = elenca(szDir, "ee_", "pdf");
    for (Path sz : ele)
      System.out.println(sz);
  }

  private Set<Path> elenca(String szDir, String szStart, String szEnd) {
    Set<Path> ele = null;
    Path      dir = Paths.get(szDir);
    if (Files.isRegularFile(dir)) {
      ele = new HashSet<Path>();
      ele.add(dir);
      return ele;
    }
    if (szStart == null || szEnd == null) {
      return ele;
    }
    try (Stream<Path> stre = Files.list(Paths.get(szDir))) {
      ele = stre //
          .filter(f -> Files.isRegularFile(f)) //
          .filter(f -> f.getFileName().toString().toLowerCase().startsWith(szStart)) //
          .filter(f -> f.getFileName().toString().toLowerCase().endsWith(szEnd)) //
          .collect(Collectors.toSet());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return ele;
  }

}
