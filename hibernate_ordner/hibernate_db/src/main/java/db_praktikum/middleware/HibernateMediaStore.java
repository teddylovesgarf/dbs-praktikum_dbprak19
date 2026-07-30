package db_praktikum.middleware;

import java.util.List;
import java.util.Properties;

import db_praktikum.entity.Product;
import db_praktikum.schnittstelle.StoreInterface;

public class HibernateMediaStore 
        implements StoreInterface {


    @Override
    public void init(Properties properties) {
        // Hibernate starten
    }


    @Override
    public void finish() {
        // SessionFactory schließen
    }


    @Override
    public Product getProduct(String id) {
        // HQL kommt später hier rein
        return null;
    }


    @Override
    public List<Product> getProducts(String pattern) {
        return null;
    }


    @Override
    public List<Product> getTopProducts(int k) {
        return null;
    }
}