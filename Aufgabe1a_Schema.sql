CREATE table produkt(

)

CREATE TABLE Filiale (
Filiale_ID #unique INT PRIMARY KEY,
Name TEXT,
Anschrift TEXT,  
); 



CREATE TABLE angebot (
    produktnummer  INTEGER     REFERENCES produkt(produkt_id) #ON DELETE CASCADE,
    filiale_id  INTEGER         REFERENCES filiale(filiale_id) #ON DELETE CASCADE,
    preis       NUMERIC(10,2)   CHECK (preis > 0),  
    zustand     VARCHAR(50)     CHECK (zustand IN ('neu', 'gebraucht', 'sehr gut', 'gut', 'akzeptabel')),
    PRIMARY KEY (produkt_id, filiale_id)
);


CREATE TABLE kunde (
    kunde_ID INTEGER PRIMARY KEY,   #SERIAL 
    name VARCHAR(50) NOT NULL,  #how long should we allow varchar 
        


        HAAAAAAAAAAAAAAAALLOOOOO 

        TEST 
         

         HABEN WIR COMMITED?????