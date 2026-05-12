-- generell stellt sich die Frage anhand welche Kriterien wir die Größe von versch. datatypes wählen
-- welche werte/pk müssen explizit als unique deklariert werden?
-- constraints und so fehlen (on cascade, not null, unique) 
-- wie generaten wir IDs? auto_increment oder? 
-- FK mitwirkende noch überall einfügen 
-- datatypes überprüfen, ich habe varchar bei unsicherheit benutzt
-- 'Autor',  book  'Künstler', cd 'Schauspieler', dvd 'Regisseur', dvd 'Filmemacher', dvd , 'publisher' buch eigene Relation, 'Label', cd 
--ck_...   = CHECK Constraint
--pk_...   = Primary Key
--fk_...   = Foreign Key
--uq_...   = Unique Constraint
--idx_...  = Index

CREATE TYPE product_type AS ENUM ('book','cd','dvd'); 
CREATE TYPE contributor_rolle as ENUM ('Autor/in' , 'Künstler/in', 'Schauspieler/in', 'Filmemacher/in', 'Regisseur/in') ; 

CREATE TABLE product(
    product_id VARCHAR(50) NOT NULL UNIQUE,
    titel VARCHAR(255) NOT NULL,
    rating INTEGER,
    salesrank INTEGER,
    picture VARCHAR(1000),
    
    CONSTRAIN pk_product PRIMARY KEY (product_id)
    CONSTRAINT ck_rating CHECK (rating >= 0 AND rating <= 5),
    CONSTRAINT ck_salesrank CHECK (salesrank > 0)
)   
--Mitwirkinde
CREATE TABLE contributor(
    contributor_id VARCHAR(20) PRIMARY KEY, 
    contributor_name VARCHAR(200), 
)

--Mitwirkende_produkt
CREATE TABLE contributor_product(
    product_id VARCHAR(20) PRIMARY KEY REFERENCES product(product_id),
    contributor_id VARCHAR(20) PRIMARY KEY REFERENCES contributor(contributor_id), 
    rolle contributor_rolle PRIMARY KEY, 
); 

--Verlag
CREATE TABLE publisher(
    publisher_id INTEGER PRIMARY KEY CHECK(publisher_id>0),   --sollen wir so die unsigned int problematik lösen?
    publisher_name VARCHAR(200), 
); 

CREATE TABLE book(
    product_id VARCHAR(20) PRIMARY KEY REFERENCES produkt(product_id)
    isbn VARCHAR(20) UNIQUE  --VARCHAR weil ISBN zeichen/buchstabe X enthalten können 
    pages INTEGER CHECK (pages>0) 
    publication_date DATE --kein Check, da auch zukünftige Buchveröffentlichungen angezeigt werden können 
    publisher_id INTEGER REFERENCES publisher(publisher_id)
); 


CREATE TABLE music_cd(
    product_id VARCHAR(20) PRIMARY KEY REFERENCES produkt(product_id),
    cd_track INTEGER,      --track numbers Wie hoch ist die durchschnittliche Anzahl von Liedern einer Musik-CD?
    cd_name VARCHAR(200) PRIMARY KEY,
    label VARCHAR(200), 
    publication_date DATE, 
); 

-- cd Liste von Titeln, die durch ihren Namen gegeben sind.

CREATE TABLE dvd(
    product_id VARCHAR(20) PRIMARY KEY REFERENCES produkt(product_id),
    format VARCHAR(100),
    runtime_minutes VARCHAR(50), 
);






CREATE TABLE angebot (
    produktnummer  INTEGER     REFERENCES produkt(product_id) #ON DELETE CASCADE,
    filiale_id  INTEGER         REFERENCES filiale(filiale_id) #ON DELETE CASCADE,
    preis       NUMERIC(10,2)   CHECK (preis > 0),  
    zustand     VARCHAR(50)     CHECK (zustand IN ('neu', 'gebraucht', 'sehr gut', 'gut', 'akzeptabel')),
    PRIMARY KEY (product_id, filiale_id)
);

--customer
CREATE TABLE customer (
    customer_ID INTEGER PRIMARY KEY, 
    customer_name VARCHAR(50) NOT NULL, 
);
        
    
--Filiale
CREATE TABLE store (
    store_id      INTEGER       NOT NULL, UNIQUE,
    store_name    VARCHAR(300)  NOT NULL,
    address       VARCHAR(1000) NOT NULL,

    CONSTRAINT pk_store
        PRIMARY KEY (store_id)
);
-- Angebot
    CREATE TABLE offer (
    product_id    VARCHAR(50)   NOT NULL,
    store_id      INTEGER       NOT NULL,
    price         DECIMAL(10,2),
    condition     VARCHAR(100),

    CONSTRAINT pk_offer
        PRIMARY KEY (product_id, store_id),

    CONSTRAINT fk_offer_product
        FOREIGN KEY (product_id)
        REFERENCES product (product_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_offer_store
        FOREIGN KEY (store_id)
        REFERENCES store (store_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT chk_offer_price
        CHECK (price IS NULL OR price >= 0)
    )
-- price IS NOT NULL  => verfügbar
-- price IS NULL      => nicht verfügbar

CREATE TABLE customer (
    customer_id    INTEGER       NOT NULL,
    customer_name           VARCHAR(300)  NOT NULL,

    CONSTRAINT pk_customer
        PRIMARY KEY (customer_id)
)
-- Einkaufswagen
CREATE TABLE cart (
    cart_id INTEGER NOT NULL,
    customer_id INTEGER NOT NULL,
    cart_time TIMESTAMP NOT NULL,
    shipping_address VARCHAR(1000) NOT NULL,
    bank_account VARCHAR(100) NOT NULL,
    CONSTRAINT pk_cart PRIMARY KEY (cart_id),
    CONSTRAINT fk_cart_customer 
        FOREIGN KEY (customer_id) 
        REFERENCES customer (customer_id)  
        ON UPDATE CASCADE 
        ON DELETE RESTRICT
)
--customer kann verschiedene Käufen; Lieferadressen; Kontodaten haben

CREATE TABLE cartposition (
    cart_id              INTEGER       NOT NULL,
    product_id           VARCHAR(50)   NOT NULL,
    store_id             INTEGER       NOT NULL,
    price_at_purchase    DECIMAL(10,2) NOT NULL,


    CONSTRAINT pk_cartposition
        PRIMARY KEY (cart_id, product_id),

    CONSTRAINT fk_cartposition_cart
        FOREIGN KEY (cart_id)
        REFERENCES cart (cart_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_cartposition_offer
        FOREIGN KEY (product_id, store_id)
        REFERENCES offer (product_id, store_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT chk_cartposition_price
        CHECK (price_at_purchase >= 0),

    CONSTRAINT chk_cartposition_quantity
        CHECK (quantity > 0)
)

CREATE TABLE review (
    review_id       INTEGER       NOT NULL,
    customer_id     INTEGER       NOT NULL,
    product_id      VARCHAR(50)   NOT NULL,
    rating          INTEGER       NOT NULL,
    review_text     VARCHAR(4000),
    review_date     DATE,

    CONSTRAINT pk_review
        PRIMARY KEY (review_id),

    CONSTRAINT fk_review_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer (customer_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_review_product
        FOREIGN KEY (product_id)
        REFERENCES product (product_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT chk_review_rating
        CHECK (rating BETWEEN 1 AND 5)
)