SELECT p.product_id, p.title
FROM offer o
JOIN product p ON p.product_id = o.product_id
GROUP BY p.product_id, p.title
HAVING COUNT(DISTINCT o.store_id) = (SELECT COUNT(*) FROM store); 


--Ursprünglich wurde bei einer bereits vorhandenen ASIN die gesamte Verarbeitung 
--durch ein return beendet. Dadurch wurden Produkte zwar nur einmal gespeichert, aber Angebote 
--für weitere Filialen gingen verloren. Wir haben die Logik so geändert, dass doppelte Produkte 
--über ON CONFLICT DO NOTHING ignoriert werden, während insertOffer() weiterhin ausgeführt wird. 
--Dadurch kann ein Produkt einmal in der Produkttabelle existieren, aber mehreren Filialen über 
--die Offer-Tabelle zugeordnet werden. 