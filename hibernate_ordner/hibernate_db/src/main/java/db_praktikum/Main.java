package db_praktikum;

import java.util.Properties;

import org.hibernate.cfg.Configuration;

import db_praktikum.resources.hibernate.properties;
import db_praktikum.middleware.HibernateMediaStore;
import db_praktikum.schnittstelle.StoreInterface;


public class Main {

    public static void main(String[] args) {

        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        sessionFactory.configure("hibernate.properties");  // Lädt automatisch!

        Properties properties = new Properties();

        StoreInterface store =
                new HibernateMediaStore();


        store.init(properties);

        System.out.println("Anwendung gestartet");


        store.getProduct("TEST-ID");


        store.finish();

        System.out.println("Anwendung beendet");
    }
}