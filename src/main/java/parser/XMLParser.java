package parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLParser {

    // Öffnet eine XML Datei und gibt ein Document zurück
    // Wird von CategoryLoader und ProductLoader benutzt
    
    public static Document parse(String filePath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));
            doc.getDocumentElement().normalize();
            return doc;
        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.out.println("Fehler beim Parsen der Datei: " + filePath);
            System.out.println(e.getMessage());
            return null;
        }
    }

    // Gibt den Textwert eines Kind-Elements zurück
    // z.B. getChildText(item, "title")
    // Gibt null zurück wenn Element nicht existiert
    
    public static String getChildText(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() > 0) {
            Node node = list.item(0);
            String text = node.getTextContent().trim();
            return text.isEmpty() ? null : text;
        }
        return null;
    }

   
    // Gibt den Wert eines Attributs zurück
    // z.B. getAttribute(item, "asin") → "B0000668PG"
    // Gibt null zurück wenn Attribut leer ist
   
    public static String getAttribute(Element element, String attributeName) {
        String value = element.getAttribute(attributeName).trim();
        return value.isEmpty() ? null : value;
    }

    
    // Gibt alle direkten Kind-Elemente mit einem bestimmten Tag zurück
    // z.B. getChildren(labels, "label") → Liste aller <label> Elemente
   
    public static List<Element> getChildren(Element parent, String tagName) {
        List<Element> result = new ArrayList<>();
        NodeList list = parent.getElementsByTagName(tagName);
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                result.add((Element) node);
            }
        }
        return result;
    }

    
    // Gibt alle <item> Elemente einer Shop-XML zurück
    // z.B. für leipzig_transformed.xml
    
    public static NodeList getItems(Document doc) {
        return doc.getElementsByTagName("item");
    }

    // Gibt das Root-Element einer XML zurück
    // z.B. <shop> oder <categories>
    
    public static Element getRoot(Document doc) {
        return doc.getDocumentElement();
    }
}