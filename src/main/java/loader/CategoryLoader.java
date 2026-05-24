package loader;

import error.ErrorLogger;
import java.sql.*;
import org.w3c.dom.*;
import parser.XMLParser;

// (sourceFile, lineNumber, entityType, attributeName, rawRecord, errorCategory, reason)

public class CategoryLoader {

    public static void load(String filePath, Connection conn) {
        Document doc = XMLParser.parse(filePath);
        if (doc == null) {
            ErrorLogger.logError(filePath, 0, "category", "file", 
                filePath, "Syntaxfehler", "Couldn't parse XML");
            return;
        }

        Element root = XMLParser.getRoot(doc);
       
        processCategory(root, null, conn);
    }


    private static void processCategory(Element element, Integer parentId, Connection conn) {
        NodeList children = element.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);

            
            if (node.getNodeType() != Node.ELEMENT_NODE) continue;
            Element child = (Element) node;

            if (child.getTagName().equals("category")) {
                String name = getDirectText(child);

                // ── Konsistenzprüfung ──────────────────────────
                if (name == null || name.isEmpty()) {
                    ErrorLogger.logError("categories.xml", 0, "category", "category_name",
            "", "NULL-Fehler", "Category name is empty");
                    continue;
                }

                // Einfügen oder vorhandene ID holen (kein Duplikat)
                Integer categoryId = insertOrGetCategory(name, parentId, conn);

                // Rekursiv Unterkategorien verarbeiten
                if (categoryId != null) {
                    processCategory(child, categoryId, conn);
                }

            } else if (child.getTagName().equals("item")) {
                // Produkt mit Kategorie verknüpfen
                String productId = child.getTextContent().trim();

                // ── Konsistenzprüfung ──────────────────────────
                if (productId.isEmpty()) {
                    ErrorLogger.logError("categories.xml", 0, "category", "category_name",
            "", "NULL-Fehler", "Product ID is empty");
                    continue;
                }

                insertProductCategory(productId, parentId, conn);
            }
        }
    }

    // ============================================================
    // Fügt Kategorie ein oder gibt ID zurück wenn sie schon existiert
    // → löst das Duplikat-Problem
    // ============================================================
    private static Integer insertOrGetCategory(String name, Integer parentId, Connection conn) {
        // Erst prüfen ob Kategorie schon existiert (gleicher Name + gleicher Parent)
        String checkSql = "SELECT category_id FROM category WHERE category_name = ? AND parent_category_id IS NOT DISTINCT FROM ?";
        try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
            stmt.setString(1, name);
            if (parentId == null) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(2, parentId);
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Existiert schon → ID zurückgeben, nicht nochmal einfügen
                return rs.getInt("category_id");
            }
        } catch (SQLException e) {
            ErrorLogger.logError(
        "categories.xml", 0, "category", "category_name",
                    name, "Constraint-Fehler", e.getMessage()
);
            return null;
        }

        // Existiert nicht → neu einfügen
        String insertSql = "INSERT INTO category (category_name, parent_category_id) VALUES (?, ?) RETURNING category_id";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setString(1, name);
            if (parentId == null) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(2, parentId);
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("category_id");
            }
        } catch (SQLException e) {
            ErrorLogger.logError(
        "categories.xml", 0, "category", "category_name",
                    name, "Constraint-Fehler", e.getMessage());
        }
        return null;
    }

    // ============================================================
    // Verknüpft ein Produkt mit einer Kategorie
    // Ignoriert wenn Produkt noch nicht in DB existiert

    private static void insertProductCategory(String productId, Integer categoryId, Connection conn) {
        if (categoryId == null) return;

        // Prüfen ob Produkt überhaupt in DB existiert
        String checkSql = "SELECT 1 FROM product WHERE product_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
            stmt.setString(1, productId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                // Produkt existiert noch nicht → überspringen
                // (wird später vom ProductLoader geladen)
                return;
            }
        } catch (SQLException e) {
            ErrorLogger.logError("categories.xml", 0, "product_category", "product_id",
                productId, "Constraint-Fehler", e.getMessage());
            return;
        }

        // Duplikat prüfen
        String checkDupSql = "SELECT 1 FROM product_category WHERE product_id = ? AND category_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(checkDupSql)) {
            stmt.setString(1, productId);
            stmt.setInt(2, categoryId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return; // Schon vorhanden
        } catch (SQLException e) {
            ErrorLogger.logError("categories.xml", 0, "product_category", "product_id",
                productId, "Constraint-Fehler", e.getMessage());
            return;
        }

        // Einfügen
        String insertSql = "INSERT INTO product_category (product_id, category_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setString(1, productId);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            ErrorLogger.logError("categories.xml", 0, "product_category", "product_id",
                productId, "Constraint-Fehler", e.getMessage());
        }
    }

    // ============================================================
    // Holt nur den direkten Textinhalt eines Elements
    // ohne den Text der Kindelemente
    // ============================================================
    private static String getDirectText(Element element) {
        StringBuilder text = new StringBuilder();
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.TEXT_NODE) {
                text.append(node.getTextContent().trim());
            }
        }
        return text.toString().trim();
    }
}