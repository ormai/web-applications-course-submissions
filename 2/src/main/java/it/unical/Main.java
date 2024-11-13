package it.unical;

import it.unical.persistence.DishDAO;
import it.unical.persistence.RestaurantDAO;

public class Main {
    public static void main(String[] args) {
        DishDAO.findAll().forEach(System.out::println);
        RestaurantDAO.findAll().forEach(System.out::println);
    }
}
