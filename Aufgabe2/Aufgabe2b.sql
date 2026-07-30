CREATE OR REPLACE FUNCTION update_avg_rating_product()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE product
            SET avg_rating_product = (
                SELECT AVG(rating)
                FROM review
                WHERE review.product_id = NEW.product_id
            )
            WHERE product_id = NEW.product_id;
    RETURN NEW; 
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER avg_rating_product
        AFTER INSERT ON review
        FOR EACH ROW
        EXECUTE FUNCTION update_avg_rating_product();

SELECT product_id, avg_rating_product FROM product WHERE product_id = 'B00004STZB';


INSERT INTO review (customer_id, product_id, rating, helpful, summary, review_text, review_date)
VALUES (1, 'B00004STZB', 5, 0, 'Ganz toll!', 'Finde das Produkt genial', CURRENT_DATE)
RETURNING *;        

SELECT product_id, avg_rating_product FROM product WHERE product_id = 'B00004STZB';