package loader;

import loader.XMLParser;
import error.ErrorLogger;
import org.w3c.dom.*;
import java.sql.*;


public class ProductLoader {
    
 public static void load(String filePath, Connection conn) {
        Document doc = XMLParser.parse(filePath);
        if (doc == null) {
            ErrorLogger.log("category", "file", "Konnte categories.xml nicht parsen");
            return;
        }

        Element root = XMLParser.getRoot(doc);
       
        processCategory(root, null, conn);
    }
}
PreparedStatement pstmt = con.prepareStatement 

