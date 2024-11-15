package it.unical.persistence;

import it.unical.model.Restaurant;

import java.util.List;

public interface RestaurantDAO {
    public List<Restaurant> findAll();

    public Restaurant findById(String name);

    public void create(Restaurant restaurant);

    public void update(Restaurant restaurant);

    public void delete(Restaurant restaurant);

    public List<Restaurant> findByDish(String dishName);
}
