package prova.readfatt;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class ProvaPdfBox {

  public static void main(String[] args) {
    ProvaPdfBox app = new ProvaPdfBox();
    String sz = "*NULL*";
    try {
      sz = app.convertPDFDocument("dati/EE_202201_1000885.pdf");
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println(sz);
  }

  public String convertPDFDocument(String url) throws FileNotFoundException, IOException {
    PDFTextStripper stripper = new PDFTextStripper();
    RandomAccessRead rndacc = new RandomAccessBufferedFileInputStream(url);
    PDFParser parser = new PDFParser(rndacc);
    parser.parse();
    PDDocument doc = parser.getPDDocument();
    String text = stripper.getText(doc);
    doc.close();
    return text;
  }

}
