package it.unical;

import it.unical.persistence.DishDAOImpl;
import it.unical.persistence.RestaurantDAOImpl;

public class Main {
    public static void main(String[] args) {
        new DishDAOImpl().findAll().forEach(System.out::println);
        new RestaurantDAOImpl().findAll().forEach(System.out::println);
    }
}
