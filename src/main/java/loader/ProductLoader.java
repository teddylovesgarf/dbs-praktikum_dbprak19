package loader;
import error.ErrorLogger;
import java.math.BigDecimal;
import java.sql.*;
import org.w3c.dom.*;
import parser.XMLParser;

public class ProductLoader {

    // Hauptmethode; wird dann von Loader.java aufgerufen
    public static void load(String filePath, Connection conn) {
        Document doc = XMLParser.parse(filePath);
        if (doc == null) {
            ErrorLogger.logError(filePath, 0, "product", "file",
                filePath, "Syntaxfehler", "Couldn't parse XML");
            return;
        }

        Element shop = XMLParser.getRoot(doc);
        if (shop == null) {
            ErrorLogger.logError(filePath, 1, "store", "shop_tag",
                filePath, "Syntaxfehler", "Root-Element <shop> fehlt.");
            return;
        }

        try {
            conn.setAutoCommit(true);

            // Store laden und ID merken
            int storeId = insertStore(shop, conn, filePath);

            // Alle items holen und verarbeiten
            NodeList items = XMLParser.getItems(doc);
            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);
                processItem(item, storeId, conn, filePath, i + 1);
            }

            
            System.out.println("Datei " + filePath + " erfolgreich geladen.");

        } catch (SQLException e) {
            ErrorLogger.logError(filePath, 0, "database", "transaction",
                "TRANSACTION_BLOCK", "Constraint-Fehler", "Rollback: " + e.getMessage());
        }
    }

    
    // Verarbeitet ein einzelnes <item> Element
    private static void processItem(Element item, int storeId, Connection conn,
                                    String filePath, int itemIndex) {
        String asin        = item.getAttribute("asin").trim();
        String pgroup      = item.getAttribute("pgroup").trim();
        String salesrankStr = item.getAttribute("salesrank").trim();
        String picture     = item.getAttribute("picture").trim();

        // Titel holen
        String title = "";
        NodeList titleNode = item.getElementsByTagName("title");
        if (titleNode.getLength() > 0) {
            title = titleNode.item(0).getTextContent().trim();
        }

        // Konsistenzprüfung - asin Pflichtfeld
        if (asin.isEmpty()) {
            ErrorLogger.logError(filePath, itemIndex, "product", "product_id",
                "", "NULL-Fehler", "asin ist leer");
            return;
        }

        // pgroup -> product_type umwandeln
        String dbProductType;
        switch (pgroup.toLowerCase()) {
             case "book":    
        case "buch":        dbProductType = "book"; break;
        case "music":   
        case "musical":     dbProductType = "music_cd"; break;
        case "dvd":         dbProductType = "dvd"; break;
        default:

        return;
        }

        // salesrank prüfen und konvertieren
        Integer salesrank = null;
        if (!salesrankStr.isEmpty()) {
            try {
                salesrank = Integer.parseInt(salesrankStr);
                if (salesrank <= 0) {
                    ErrorLogger.logError(filePath, itemIndex, "product", "salesrank",
                        salesrankStr, "Constraint-Fehler", "Salesrank muss > 0 sein bei ASIN: " + asin);
                    salesrank = null;
                }
            } catch (NumberFormatException e) {
                salesrank = null;
            }
        }

        // Duplikat prüfen
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT 1 FROM product WHERE product_id = ?")) {
            stmt.setString(1, asin);
            if (stmt.executeQuery().next()) return; // bereits vorhanden
        } catch (SQLException e) {
            ErrorLogger.logError(filePath, itemIndex, "product", "product_id",
                asin, "Constraint-Fehler", e.getMessage());
            return;
        }

        // INSERT product
        String insertSql = "INSERT INTO product (product_id, title, product_type, salesrank, picture) " +
                           "VALUES (?, ?, ?::product_type_enum, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setString(1, asin);
            stmt.setString(2, title);
            stmt.setString(3, dbProductType);
            if (salesrank == null) stmt.setNull(4, Types.INTEGER); else stmt.setInt(4, salesrank);
            stmt.setString(5, picture.isEmpty() ? null : picture);
            stmt.executeUpdate();
            ErrorLogger.incrementLoaded();
        } catch (SQLException e) {
            ErrorLogger.logError(filePath, itemIndex, "product", "product_id",
                asin, "Constraint-Fehler", e.getMessage());
            return;
        }

        // Je nach Typ verzweigen
        switch (dbProductType) {
            case "book":     insertBook(item, asin, conn, filePath, itemIndex); break;
            case "music_cd": insertMusicCD(item, asin, conn, filePath, itemIndex); break;
            case "dvd":      insertDVD(item, asin, conn, filePath, itemIndex); break;
        }

        // Similar products und Angebot laden
        insertSimilarProducts(item, asin, conn, filePath, itemIndex);
        insertOffer(item, asin, storeId, conn, filePath, itemIndex);
    }

    // Lädt Store Informationen aus <shop> Attributen
    private static int insertStore(Element shop, Connection conn, String filePath) {
        String name   = shop.getAttribute("name").trim();
        String street = shop.getAttribute("street").trim();
        String zip    = shop.getAttribute("zip").trim();

        // Duplikat prüfen
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT store_id FROM store WHERE store_name = ?")) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("store_id");
        } catch (SQLException ignored) {}

        // Neu anlegen
        String insertSql = "INSERT INTO store (store_name, street, zip) VALUES (?, ?, ?) RETURNING store_id";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setString(1, name);
            stmt.setString(2, street.isEmpty() ? null : street);
            stmt.setString(3, zip.isEmpty() ? null : zip);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            ErrorLogger.logError(filePath, 1, "store", "store_name",
                name, "Constraint-Fehler", e.getMessage());
        }
        return 0;
    }


    // Lädt ein Buch und seine Abhängigkeiten

    private static void insertBook(Element item, String productId, Connection conn,
                                   String filePath, int itemIndex) {
        // bookspec Element holen
        NodeList bookspecList = item.getElementsByTagName("bookspec");
        if (bookspecList.getLength() == 0) return;
        Element bookspec = (Element) bookspecList.item(0);

        // isbn → Attribut "val" von <isbn>
        String isbn = null;
        NodeList isbnList = bookspec.getElementsByTagName("isbn");
        if (isbnList.getLength() > 0) {
            isbn = ((Element) isbnList.item(0)).getAttribute("val").trim();
            if (isbn.isEmpty()) isbn = null;
        }

        // pages -> Textinhalt von <pages>
        Integer pages = null;
        String pagesStr = XMLParser.getChildText(bookspec, "pages");
        if (pagesStr != null) {
            try {
                pages = Integer.parseInt(pagesStr);
                if (pages <= 0) pages = null;
            } catch (NumberFormatException e) {
                pages = null;
            }
        }

        // publication_date -> Attribut "date" von <publication>
        java.sql.Date pubDate = null;
        NodeList pubList = bookspec.getElementsByTagName("publication");
        if (pubList.getLength() > 0) {
            String pubDateStr = ((Element) pubList.item(0)).getAttribute("date").trim();
            if (!pubDateStr.isEmpty()) {
                try {
                    pubDate = java.sql.Date.valueOf(pubDateStr);
                } catch (IllegalArgumentException e) {
                    ErrorLogger.logError(filePath, itemIndex, "book", "publication_date",
                        pubDateStr, "Typfehler", "Ungültiges Datum bei ASIN: " + productId);
                }
            }
        }

        // publisher -> Attribut "name" von erstem <publisher>
        String publisherName = null;
        NodeList pubNameList = item.getElementsByTagName("publisher");
        if (pubNameList.getLength() > 0) {
            publisherName = ((Element) pubNameList.item(0)).getAttribute("name").trim();
            if (publisherName.isEmpty()) publisherName = null;
        }
        Integer publisherId = insertPublisher(publisherName, conn, filePath, itemIndex);

        // INSERT book
        String sql = "INSERT INTO book (product_id, isbn, pages, publication_date, publisher_id) " +
                     "VALUES (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productId);
            stmt.setString(2, isbn);
            if (pages == null) stmt.setNull(3, Types.INTEGER); else stmt.setInt(3, pages);
            stmt.setDate(4, pubDate);
            if (publisherId == null) stmt.setNull(5, Types.INTEGER); else stmt.setInt(5, publisherId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            ErrorLogger.logError(filePath, itemIndex, "book", "product_id",
                productId, "Constraint-Fehler", e.getMessage());
            return;
        }

        // Authors laden
        NodeList authors = item.getElementsByTagName("author");
        for (int i = 0; i < authors.getLength(); i++) {
            String authorName = ((Element) authors.item(i)).getAttribute("name").trim();
            if (!authorName.isEmpty()) {
                insertContributor(authorName, productId, "Autor/in", conn);
            }
        }
    }

    
    // Lädt eine MusicCD und ihre Abhängigkeiten
    private static void insertMusicCD(Element item, String productId, Connection conn,
                                      String filePath, int itemIndex) {
        // label holen
        String label = null;
        NodeList labelList = item.getElementsByTagName("label");
        if (labelList.getLength() > 0) {
            label = ((Element) labelList.item(0)).getAttribute("name").trim();
            if (label.isEmpty()) label = null;
        }

        // publication_date holen
        java.sql.Date pubDate = null;
        String pubDateStr = XMLParser.getChildText((Element) item.getElementsByTagName("musicspec").item(0), "releasedate");
        if (pubDateStr != null && !pubDateStr.isEmpty()) {
            try {
                pubDate = java.sql.Date.valueOf(pubDateStr);
            } catch (IllegalArgumentException e) {
                ErrorLogger.logError(filePath, itemIndex, "music_cd", "publication_date",
                    pubDateStr, "Typfehler", "Ungültiges Datum bei ASIN: " + productId);
            }
        }

        // Künstler prüfen; mindestens einer erforderlich
        NodeList artists = item.getElementsByTagName("artist");
        if (artists.getLength() == 0) {
            ErrorLogger.logError(filePath, itemIndex, "music_cd", "artist",
                productId, "Constraint-Fehler", "Mindestens ein Künstler erforderlich bei ASIN: " + productId);
        }

        // INSERT music_cd
        String sql = "INSERT INTO music_cd (product_id, label, publication_date) " +
                     "VALUES (?, ?, ?) ON CONFLICT DO NOTHING";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productId);
            stmt.setString(2, label);
            stmt.setDate(3, pubDate);
            stmt.executeUpdate();
        } catch (SQLException e) {
            ErrorLogger.logError(filePath, itemIndex, "music_cd", "product_id",
                productId, "Constraint-Fehler", e.getMessage());
            return;
        }

        // Tracks laden
        insertTracks(item, productId, conn);

        // Künstler laden
        for (int i = 0; i < artists.getLength(); i++) {
            String artistName = ((Element) artists.item(i)).getAttribute("name").trim();
            if (!artistName.isEmpty()) {
                insertContributor(artistName, productId, "Künstler/in", conn);
            }
        }
    }

    
    // Lädt eine DVD und ihre Abhängigkeiten
    
    private static void insertDVD(Element item, String productId, Connection conn,
                                   String filePath, int itemIndex) {
        // format, runtime, region_code holen
        String format = null;
        NodeList formatList = item.getElementsByTagName("format");
        if (formatList.getLength() > 0) {
            format = formatList.item(0).getTextContent().trim();
            if (format.isEmpty()) format = null;
        }

        Integer runtime = null;
        String runtimeStr = XMLParser.getChildText(
            (Element) item.getElementsByTagName("dvdspec").item(0), "runtime_minutes");
        if (runtimeStr != null) {
            try {
                runtime = Integer.parseInt(runtimeStr);
                if (runtime <= 0) {
                    ErrorLogger.logError(filePath, itemIndex, "dvd", "runtime_minutes",
                        runtimeStr, "Constraint-Fehler", "Laufzeit muss > 0 sein.");
                    runtime = null;
                }
            } catch (NumberFormatException ignored) {}
        }

        Integer regionCode = null;
        String regionStr = XMLParser.getChildText(
            (Element) item.getElementsByTagName("dvdspec").item(0), "region_code");
        if (regionStr != null) {
            try {
                regionCode = Integer.parseInt(regionStr);
                if (regionCode < 0 || regionCode > 8) {
                    ErrorLogger.logError(filePath, itemIndex, "dvd", "region_code",
                        regionStr, "Constraint-Fehler", "Region Code außerhalb Bereich (0-8).");
                    regionCode = null;
                }
            } catch (NumberFormatException ignored) {}
        }

        // INSERT dvd
        String sql = "INSERT INTO dvd (product_id, format, runtime_minutes, region_code) " +
                     "VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productId);
            stmt.setString(2, format);
            if (runtime == null) stmt.setNull(3, Types.INTEGER); else stmt.setInt(3, runtime);
            if (regionCode == null) stmt.setNull(4, Types.INTEGER); else stmt.setInt(4, regionCode);
            stmt.executeUpdate();
        } catch (SQLException e) {
            ErrorLogger.logError(filePath, itemIndex, "dvd", "product_id",
                productId, "Constraint-Fehler", e.getMessage());
            return;
        }

        // Mitwirkende laden
        NodeList actors = item.getElementsByTagName("actor");
        for (int i = 0; i < actors.getLength(); i++) {
            String name = ((Element) actors.item(i)).getAttribute("name").trim();
            if (!name.isEmpty()) insertContributor(name, productId, "Schauspieler/in", conn);
        }

        NodeList directors = item.getElementsByTagName("director");
        for (int i = 0; i < directors.getLength(); i++) {
            String name = ((Element) directors.item(i)).getAttribute("name").trim();
            if (!name.isEmpty()) insertContributor(name, productId, "Regisseur/in", conn);
        }

        NodeList creators = item.getElementsByTagName("creator");
        for (int i = 0; i < creators.getLength(); i++) {
            String name = ((Element) creators.item(i)).getAttribute("name").trim();
            if (!name.isEmpty()) insertContributor(name, productId, "Filmemacher/in", conn);
        }
    }

    
    // Lädt einen Publisher und gibt seine ID zurück

    private static Integer insertPublisher(String name, Connection conn,
                                           String filePath, int itemIndex) {
        if (name == null || name.isEmpty()) return null;

        // Duplikat prüfen
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT publisher_id FROM publisher WHERE publisher_name = ?")) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("publisher_id");
        } catch (SQLException ignored) {}

        // Neu anlegen
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO publisher (publisher_name) VALUES (?) RETURNING publisher_id")) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            ErrorLogger.logError(filePath, itemIndex, "publisher", "publisher_name",
                name, "Constraint-Fehler", e.getMessage());
        }
        return null;
    }

    // Lädt einen Contributor und verknüpft ihn mit dem Produkt

    private static void insertContributor(String name, String productId,
                                          String rolle, Connection conn) {
        if (name == null || name.isEmpty()) return;

        // Contributor suchen oder anlegen
        int contributorId = 0;
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT contributor_id FROM contributor WHERE contributor_name = ?")) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) contributorId = rs.getInt("contributor_id");
        } catch (SQLException ignored) {}

        if (contributorId == 0) {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO contributor (contributor_name) VALUES (?) RETURNING contributor_id")) {
                stmt.setString(1, name);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) contributorId = rs.getInt(1);
            } catch (SQLException e) {
                return;
            }
        }

        // Verknüpfung anlegen
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO contributor_product (product_id, contributor_id, rolle) " +
                "VALUES (?, ?, ?::contributor_rolle) ON CONFLICT DO NOTHING")) {
            stmt.setString(1, productId);
            stmt.setInt(2, contributorId);
            stmt.setString(3, rolle);
            stmt.executeUpdate();
        } catch (SQLException ignored) {}
    }

   
    // Lädt alle Tracks einer CD
    
    private static void insertTracks(Element item, String productId, Connection conn) {
        NodeList tracksList = item.getElementsByTagName("tracks");
        if (tracksList.getLength() == 0) return;

        Element tracksElem = (Element) tracksList.item(0);
        NodeList tracks = tracksElem.getElementsByTagName("title");

        for (int i = 0; i < tracks.getLength(); i++) {
            String trackName = tracks.item(i).getTextContent().trim();
            if (trackName.isEmpty()) continue;

            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO cd_title (product_id, title_number, title_name) " +
                    "VALUES (?, ?, ?) ON CONFLICT DO NOTHING")) {
                stmt.setString(1, productId);
                stmt.setInt(2, i + 1);          // Reihenfolge im XML = Tracknummer
                stmt.setString(3, trackName);
                stmt.executeUpdate();
            } catch (SQLException ignored) {}
        }
    }

    
    // Lädt ähnliche Produkte
    private static void insertSimilarProducts(Element item, String productId,
                                              Connection conn, String filePath, int itemIndex) {
        NodeList sims = item.getElementsByTagName("sim_product");
        for (int i = 0; i < sims.getLength(); i++) {
            Element sim = (Element) sims.item(i);
            NodeList asinList = sim.getElementsByTagName("asin");
            if (asinList.getLength() == 0) continue;
            String simAsin = asinList.item(0).getTextContent().trim();
            if (simAsin.isEmpty()) continue;

            // Nur einfügen wenn ähnliches Produkt existiert
            if (!productExists(simAsin, conn)) {
                ErrorLogger.logError(filePath, itemIndex, "similar_products", "similar_product_id",
                    simAsin, "Constraint-Fehler", "Ähnliches Produkt existiert nicht: " + simAsin);
                continue;
            }

            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO similar_products (product_id, similar_product_id) " +
                    "VALUES (?, ?) ON CONFLICT DO NOTHING")) {
                stmt.setString(1, productId);
                stmt.setString(2, simAsin);
                stmt.executeUpdate();
            } catch (SQLException ignored) {}
        }
    }

    
    // Lädt das Angebot eines Produkts in einer Filiale

    private static void insertOffer(Element item, String productId, int storeId, Connection conn, String filePath, int itemIndex) {
        NodeList priceNodes = item.getElementsByTagName("price");
        if (priceNodes.getLength() == 0) return;

        Element priceElem = (Element) priceNodes.item(0);
        String multStr    = priceElem.getAttribute("mult").trim();
        String state      = priceElem.getAttribute("state").trim();
        String currency   = priceElem.getAttribute("currency").trim();
        String rawPrice   = priceElem.getTextContent().trim();

        // Preis berechnen (rawPrice * mult)
        BigDecimal price = null;
        if (!rawPrice.isEmpty()) {
            try {
                double mult = multStr.isEmpty() ? 0.01 : Double.parseDouble(multStr);
                price = new BigDecimal(rawPrice).multiply(BigDecimal.valueOf(mult));
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    ErrorLogger.logError(filePath, itemIndex, "offer", "price",
                        rawPrice, "Constraint-Fehler", "Preis ist <= 0 bei ASIN: " + productId);
                    price = null;
                }
            } catch (NumberFormatException e) {
                ErrorLogger.logError(filePath, itemIndex, "offer", "price",
                    rawPrice, "Typfehler", "Preis konnte nicht konvertiert werden.");
            }
        }

        // INSERT offer
        String sql = "INSERT INTO offer (product_id, store_id, price, condition, currency) " +
                     "VALUES (?, ?, ?, ?, ?) ON CONFLICT (product_id, store_id) DO UPDATE SET price = EXCLUDED.price";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productId);
            stmt.setInt(2, storeId);
            if (price == null) stmt.setNull(3, Types.DECIMAL); else stmt.setBigDecimal(3, price);
            stmt.setString(4, state.isEmpty() ? null : state);
            stmt.setString(5, currency.isEmpty() ? null : currency);
            stmt.executeUpdate();
        } catch (SQLException e) {
            ErrorLogger.logError(filePath, itemIndex, "offer", "price",
                rawPrice, "Constraint-Fehler", e.getMessage());
        }
    }

    
    // Hilfsmethode; prüft ob ein Produkt in der DB existiert
    private static boolean productExists(String asin, Connection conn) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT 1 FROM product WHERE product_id = ?")) {
            stmt.setString(1, asin);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }
}