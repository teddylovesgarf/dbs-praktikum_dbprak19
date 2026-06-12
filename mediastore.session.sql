-- Anfrage 1
SELECT
    product_type,
    COUNT(*) AS anzahl
FROM product
GROUP BY product_type
ORDER BY product_type;


-- Anfrage 2
WITH product_ratings AS (
    SELECT 
        p.product_type,
        p.product_id,
        AVG(r.rating) AS average_rating,
        COUNT(r.review_id) AS review_count
    FROM product p
    JOIN review r 
        ON p.product_id = r.product_id
    GROUP BY p.product_type, p.product_id
),
ranked_products AS (
    SELECT 
        product_type,
        product_id,
        average_rating,
        review_count,
        ROW_NUMBER() OVER (
            PARTITION BY product_type 
            ORDER BY average_rating DESC, review_count DESC, product_id
        ) AS ranking
    FROM product_ratings
)
SELECT
    product_type,
    product_id,
    average_rating 
FROM ranked_products
WHERE ranking <= 5
ORDER BY product_type, average_rating DESC, product_id;


--Anfrage 3
SELECT
    p.product_id
FROM product p
WHERE NOT EXISTS (
    SELECT 1
    FROM offer o
    WHERE o.product_id = p.product_id
      AND o.price IS NOT NULL
);

-- Anfrage 4
SELECT product_id FROM offer
WHERE price IS NOT NULL
GROUP BY product_id
HAVING MAX(price) > 2 * MIN(price);


--Anfrage 5
SELECT DISTINCT product_id FROM review p

WHERE EXISTS (
    SELECT * FROM review r 
    WHERE r.product_id = p.product_id 
    AND r.rating = 5)

AND EXISTS (
    SELECT * FROM review r 
    WHERE r.product_id = p.product_id 
    AND r.rating = 1);


--Anfrage 6
SELECT 
COUNT (*) AS anzahl_produkte_ohne_rezension
FROM product p
WHERE NOT EXISTS (
    SELECT 1
    FROM review r
    WHERE r.product_id = p.product_id
);

--Anfrage 7
SELECT
    c.customer_name,
    COUNT(r.review_id) AS anzahl_reviews
FROM customer c
JOIN review r
    ON c.customer_id = r.customer_id
GROUP BY c.customer_id, c.customer_name
HAVING COUNT(r.review_id) >= 10
ORDER BY anzahl_reviews DESC, c.customer_name;

--Anfrage 8
SELECT DISTINCT a.contributor_name
FROM contributor a
JOIN contributor_product cp_book
    ON a.contributor_id= cp_book.contributor_id
    --alle Personen holen, die ein Eintrag in contributor_product haben
    --Hier werden Mitwirkende mit ihren Produktbeteiligungen verbunden.
JOIN product p_book
    ON cp_book.product_id = p_book.product_id
    -- Hier werden diese Produktbeteiligungen mit den Produktdaten verbunden, damit man den Produkttyp prüfen kann.
JOIN contributor_product cp_dvd_music_contributor
    ON a.contributor_id = cp_dvd_music_contributor.contributor_id
-- a.contributor_id von oben wird mit allen seine/ihre Produktbeteiligungen verbunden
JOIN product p_dvd_music
    ON cp_dvd_music_contributor.product_id = p_dvd_music.product_id
WHERE cp_book.rolle = 'Autor/in'
    AND p_book.product_type = 'book'
    AND p_dvd_music.product_type IN ('dvd', 'music_cd') --ODER Abfrage
ORDER BY a.contributor_name;

--Anfrage 9
SELECT AVG(anzahl_title) AS average_titel_on_cd
FROM(
SELECT product_id, COUNT(*) AS anzahl_title
FROM cd_title
    GROUP BY product_id
    ) AS titel_pro_cd;
    

--Anfrage 10

WITH RECURSIVE oberkategorie AS ( 
    SELECT category_id, category_name, parent_category_id, category_id AS start_category_id
    FROM category
    -- WHERE parent_category_id IS NOT NULL war falsch, da Hauptkategorien selbst nicht als Kategorie 
    -- dadurch geladen wurden und Produkte die eine Hauptkategorie unmittelbar als Oberkategorie hatten
    -- wurden nicht geladen

    UNION ALL 
    
    SELECT c.category_id, c.category_name, c.parent_category_id, ok.start_category_id
    FROM category c
    INNER JOIN Oberkategorie ok ON c.category_id = ok.parent_category_id
),

product_hauptkategorie AS (

    SELECT ok.category_id, pc.product_id
    FROM product_category pc 
    JOIN oberkategorie ok ON pc.category_id = ok.start_category_id
    WHERE ok.parent_category_id IS NULL --Hier erst Hauptkategorie definieren

    )

SELECT DISTINCT sp.product_id, sp.similar_product_id 
FROM similar_products sp 
JOIN product_hauptkategorie ph ON sp.product_id = ph.product_id 
JOIN product_hauptkategorie ph2 ON sp.similar_product_id = ph2.product_id 
WHERE ph2.category_id != ph.category_id;

-- Anfrage 11
SELECT p.product_id, p.title
FROM offer o
JOIN product p ON p.product_id = o.product_id
GROUP BY p.product_id, p.title
HAVING COUNT(DISTINCT o.store_id) = (SELECT COUNT(*) FROM store);

-- Anfrage 12 

WITH all_products AS (
    SELECT product_id
    FROM offer
    GROUP BY product_id
    HAVING COUNT(DISTINCT store_id) = (SELECT COUNT(*) FROM store)
),
min_prices AS (
    SELECT product_id, MIN(price) AS min_price
    FROM offer
    GROUP BY product_id
),
min_price_leipzig AS (
    SELECT DISTINCT o.product_id
    FROM offer o
    JOIN store s ON s.store_id = o.store_id
    JOIN min_prices mp ON mp.product_id = o.product_id AND mp.min_price = o.price
    WHERE s.store_name = 'Leipzig'
)
SELECT ROUND(
    100.0 * COUNT(mpl.product_id) / COUNT(*),
    2
) AS percentage_leipzig
FROM all_products ap
LEFT JOIN min_price_leipzig mpl ON ap.product_id = mpl.product_id;