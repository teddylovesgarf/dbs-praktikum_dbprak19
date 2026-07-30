package db_praktikum;

import java.util.Properties;

import db_praktikum.middleware.HibernateMediaStore;
import db_praktikum.schnittstelle.StoreInterface;


public class Main {

    public static void main(String[] args) {

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