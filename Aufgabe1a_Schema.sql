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

CREATE TYPE product_type_enum AS ENUM ('book','music_cd','dvd'); 
CREATE TYPE contributor_rolle AS ENUM ('Autor/in' , 'Künstler/in', 'Schauspieler/in', 'Filmemacher/in', 'Regisseur/in') ; 

CREATE TABLE product(
    product_id      VARCHAR(50) NOT NULL, 
    title           VARCHAR(255) NOT NULL,
    product_type product_type_enum NOT NULL,
    salesrank       INTEGER,
    picture         VARCHAR(1000),
    -- rating wird aus reviews berechnet, daher kein Attribut in der Produkt-Tabelle
    
    CONSTRAINT pk_product 
        PRIMARY KEY (product_id),

    CONSTRAINT ck_product_type 
        CHECK (product_type IN ('book', 'music_cd', 'dvd')), -- eigentlich über enum definiert, aber sicherheitshalber nochmal check constraint

    CONSTRAINT ck_product_salesrank 
        CHECK (salesrank IS NULL OR salesrank > 0)
);  

--Mitwirkinde
CREATE TABLE contributor(
    contributor_id      INTEGER  NOT NULL, 
    contributor_name    VARCHAR(200) NOT NULL,

    CONSTRAINT pk_contributor 
        PRIMARY KEY (contributor_id),

    CONSTRAINT ck_contributor_id 
        CHECK (contributor_id > 0)
);

--Mitwirkende_produkt
CREATE TABLE contributor_product(
    product_id VARCHAR(50) NOT NULL,
    contributor_id INTEGER NOT NULL,
    rolle contributor_rolle NOT NULL,

    CONSTRAINT pk_contributor_product
        PRIMARY KEY (product_id, contributor_id, rolle),

    CONSTRAINT fk_contributor_product_product
        FOREIGN KEY (product_id)
        REFERENCES product (product_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_contributor_product_contributor
        FOREIGN KEY (contributor_id)
        REFERENCES contributor (contributor_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
); 

--Verlag
CREATE TABLE publisher(
    publisher_id INTEGER    NOT NULL,   
    publisher_name VARCHAR(200) NOT NULL,
    CONSTRAINT pk_publisher 
        PRIMARY KEY (publisher_id),
    CONSTRAINT ck_publisher_id
        CHECK (publisher_id > 0)--sollen wir so die unsigned int problematik lösen? Finde ich gut!
); 

CREATE TABLE book(
    product_id VARCHAR(50) NOT NULL,
    isbn VARCHAR(20) UNIQUE,  --VARCHAR weil ISBN zeichen/buchstabe X enthalten können 
    pages INTEGER CHECK ( pages IS NULL OR pages>0), -- Seitenzahl kann auch unbekannt sein, daher NULL 
    publication_date DATE, --kein Check, da auch zukünftige Buchveröffentlichungen angezeigt werden können 
    publisher_id INTEGER NOT NULL,

    CONSTRAINT pk_book
        PRIMARY KEY (product_id),

    CONSTRAINT fk_book_product
        FOREIGN KEY (product_id)
        REFERENCES product (product_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

        CONSTRAINT fk_book_publisher
        FOREIGN KEY (publisher_id)
        REFERENCES publisher (publisher_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
); 


CREATE TABLE music_cd(
    product_id VARCHAR(50) NOT NULL,
    cd_name VARCHAR(200) NOT NULL, --könnte nicht funktionieren, wenn mehrere CDs den gleichen Namen haben, muss nicht unbedingt eindeutig sein 
    label VARCHAR(200), 
    publication_date DATE, 

    CONSTRAINT pk_music_cd
        PRIMARY KEY (product_id),

    CONSTRAINT fk_music_cd_product
        FOREIGN KEY (product_id)
        REFERENCES product (product_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
); 

-- cd Liste von Titeln, die durch ihren Namen gegeben sind.
-- music_cd 1:n cd_title, da eine CD mehrere Titel haben kann, aber ein Titel nur zu einer CD gehört.
          --track numbers Wie hoch ist die durchschnittliche Anzahl von Liedern einer Musik-CD?

CREATE TABLE cd_title (
    product_id VARCHAR(50) NOT NULL,
    title_number INTEGER NOT NULL,
    title_name VARCHAR(300) NOT NULL,

    CONSTRAINT pk_cd_title
        PRIMARY KEY (product_id, title_number),

    CONSTRAINT fk_cd_title_music_cd
        FOREIGN KEY (product_id)
        REFERENCES music_cd (product_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT ck_cd_title_number
        CHECK (title_number > 0)
);

CREATE TABLE dvd(
    product_id VARCHAR(50) NOT NULL,
    format VARCHAR(100),
    runtime_minutes INTEGER,
    region_code VARCHAR(50) ,

    CONSTRAINT pk_dvd
        PRIMARY KEY (product_id),

      CONSTRAINT fk_dvd_product
        FOREIGN KEY (product_id)
        REFERENCES product (product_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT ck_dvd_runtime
        CHECK (runtime_minutes IS NULL OR runtime_minutes > 0) --falls unbekannt 
);




--customer
CREATE TABLE customer (
    customer_ID INTEGER PRIMARY KEY, 
    customer_name VARCHAR(50) NOT NULL, 
);
        
    
--Filiale
CREATE TABLE store (
    store_id      INTEGER       NOT NULL,
    store_name    VARCHAR(300)  NOT NULL,
    store_address       VARCHAR(1000) NOT NULL,

    CONSTRAINT pk_store
        PRIMARY KEY (store_id),

    CONSTRAINT ck_store_id
        CHECK (store_id > 0)
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
    );
-- price IS NOT NULL  => verfügbar
-- price IS NULL      => nicht verfügbar


-- Einkaufswagen
CREATE TABLE cart (
    cart_id         INTEGER NOT NULL,
    customer_id     INTEGER NOT NULL,
    cart_time       TIMESTAMP NOT NULL,
    shipping_address VARCHAR(1000) NOT NULL,
    bank_account    VARCHAR(100) NOT NULL,

    CONSTRAINT pk_cart PRIMARY KEY (cart_id),
    CONSTRAINT fk_cart_customer 
        FOREIGN KEY (customer_id) 
        REFERENCES customer (customer_id)  
        ON UPDATE CASCADE 
        ON DELETE RESTRICT
);
--customer kann verschiedene Käufen; Lieferadressen; Kontodaten haben

CREATE TABLE cartposition (
    cart_id              INTEGER       NOT NULL,
    product_id           VARCHAR(50)   NOT NULL,
    store_id             INTEGER       NOT NULL,
    price_at_purchase    DECIMAL(10,2) NOT NULL,
    quantity             INTEGER       NOT NULL,


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
        
);


CREATE TABLE review (
    review_id       VARCHAR(50)   NOT NULL,
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
);