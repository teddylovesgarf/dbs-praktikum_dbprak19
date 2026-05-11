-- generell stellt sich die Frage anhand welche Kriterien wir die Größe von versch. datatypes wählen
-- welche werte/pk müssen explizit als unique deklariert werden?
-- constraints und so fehlen (on cascade, not null, unique) 
-- wie generaten wir IDs? auto_increment oder? 
-- FK mitwirkende noch überall einfügen 
-- datatypes überprüfen, ich habe varchar bei unsicherheit benutzt
-- 'Autor',  buch  'Künstler', cd 'Schauspieler', dvd 'Regisseur', dvd 'Filmemacher', dvd , 'VERLAG' buch eigene Relation, 'Label', cd 

CREATE TYPE produkt_typ AS ENUM ('buch','cd','dvd'); 
CREATE TYPE mitwirkende_rolle as ENUM ('Autor/in' , 'Künstler/in', 'Schauspieler/in', 'Filmemacher/in', 'Regisseur/in') ; 


CREATE TABLE mitwirkinde(
    mitwirkende_id VARCHAR(20) PRIMARY KEY, 
    mitwirkende_name VARCHAR(200), 
);

CREATE TABLE mitwirkende_produkt(
    produkt_id VARCHAR(20) PRIMARY KEY REFERENCES produkt(produkt_id),
    mitwirkende_id VARCHAR(20) PRIMARY KEY REFERENCES mitwirkende_id, 
    rolle mitwirkende_rolle PRIMARY KEY, 
); 

 CREATE TABLE poduct(
    product_id VARCHAR(50) NOT NULL PRIMARY KEY,
    titel VARCHAR(255) NOT NULL,
    rating NUMERIC(3,2),  --store as percentage [00,00 - 100,00] 
    salesrank INTEGER,
    picture VARCHAR(1000),
    
    CONSTRAIN pk_product PRIMARY KEY (product_id)
    CONSTRAINT ck_rating CHECK (rating >= 0 AND rating <= 5),
    CONSTRAINT ck_salesrank CHECK (salesrank > 0)       
); 

CREATE TABLE verlag(
    verlag_id INTEGER PRIMARY KEY CHECK(verlag_id>0),   --sollen wir so die unsigned int problematik lösen?
    verlag_name VARCHAR(200), 
); 

CREATE TABLE buch(
    produkt_id VARCHAR(20) PRIMARY KEY REFERENCES produkt(produkt_id)
    isbn VARCHAR(20) UNIQUE  --VARCHAR weil ISBN zeichen/buchstabe X enthalten können 
    seitenzahl INTEGER CHECK (seitenzahl>0) 
    erscheinungsdatum DATE --kein Check, da auch zukünftige Buchveröffentlichungen angezeigt werden können 
    verlag_id INTEGER REFERENCES verlag(verlag_id)
); 


CREATE TABLE musik_cd(
    produkt_id VARCHAR(20) PRIMARY KEY REFERENCES produkt(produkt_id),
    cd_track INTEGER,      --track numbers Wie hoch ist die durchschnittliche Anzahl von Liedern einer Musik-CD?
    cd_name VARCHAR(200) PRIMARY KEY,
    label VARCHAR(200), 
    erscheinungsdatum DATE, 
); 

-- cd Liste von Titeln, die durch ihren Namen gegeben sind.

CREATE TABLE dvd(
    produkt_id VARCHAR(20) PRIMARY KEY REFERENCES produkt(produkt_id),
    format VARCHAR(100),
    laufzeit VARCHAR(50), 
);




CREATE TABLE Filiale (
filiale_ID #unique INT PRIMARY KEY,
name TEXT,
anschrift TEXT,  
); 



CREATE TABLE angebot (
    produktnummer  INTEGER     REFERENCES produkt(produkt_id) #ON DELETE CASCADE,
    filiale_id  INTEGER         REFERENCES filiale(filiale_id) #ON DELETE CASCADE,
    preis       NUMERIC(10,2)   CHECK (preis > 0),  
    zustand     VARCHAR(50)     CHECK (zustand IN ('neu', 'gebraucht', 'sehr gut', 'gut', 'akzeptabel')),
    PRIMARY KEY (produkt_id, filiale_id)
);


CREATE TABLE kunde (
    kunde_ID INTEGER PRIMARY KEY, 
    name VARCHAR(50) NOT NULL, 
);
        
 CREATE TABLE poduct(
    product_id VARCHAR(50) NOT NULL PRIMARY KEY,
    titel VARCHAR(255) NOT NULL,
    rating INTEGER,
    salesrank INTEGER,
    picture VARCHAR(1000),
    
    CONSTRAIN pk_product PRIMARY KEY (product_id)
    CONSTRAINT ck_rating CHECK (rating >= 0 AND rating <= 5),
    CONSTRAINT ck_salesrank CHECK (salesrank > 0)
)       