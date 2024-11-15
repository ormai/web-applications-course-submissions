package it.unical.persistence;

import it.unical.model.Dish;

import java.util.List;

public interface DishDAO {
    public List<Dish> findAll();

    public Dish findById(String name);

    public void create(Dish dish);

    public void update(Dish dish);

    public void delete(Dish dish);

    public List<Dish> findByRestaurant(String restaurantName);
}
