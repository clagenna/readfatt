package prova.readfatt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class PdfToTextTika {

  public static void main(String[] args) {
    // Create a content handler
    BodyContentHandler contenthandler = new BodyContentHandler();
    // Create a file in local directory
    File f = new File("dati/EE_202201_1000885.pdf");
    // Create a file input stream on specified path with the created file
    FileInputStream fstream = null;
    try {
      fstream = new FileInputStream(f);
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    }
    // Create an object of type Metadata to use
    Metadata data = new Metadata();
    // Create a context parser for the pdf document
    ParseContext context = new ParseContext();
    // PDF document can be parsed using the PDFparser class
    PDFParser pdfparser = new PDFParser();
    // Method parse invoked on PDFParser class
    try {
      pdfparser.parse(fstream, contenthandler, data, context);
    } catch (IOException | SAXException | TikaException e) {
      e.printStackTrace();
    }
    System.out.println("Extracting contents :" + contenthandler.toString());
  }

}
