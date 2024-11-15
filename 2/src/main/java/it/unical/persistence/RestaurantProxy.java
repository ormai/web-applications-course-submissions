package it.unical.persistence;

import it.unical.model.Dish;
import it.unical.model.Restaurant;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RestaurantProxy extends Restaurant {
    public RestaurantProxy(String name, String description, String location) {
        super(name, description, location);
    }

    public RestaurantProxy(Restaurant restaurant) {
        super(restaurant.getName(), restaurant.getDescription(), restaurant.getLocation());
    }

    @Override
    public List<Dish> getDishes() {
        try (PreparedStatement ps = DataSource.getConnection().prepareStatement("""
                SELECT dish.name, dish.ingredients
                FROM restaurant
                    INNER JOIN restaurant_dish rd on restaurant.name = rd.restaurant
                    INNER JOIN dish on dish.name = rd.dish
                """)) {
            ResultSet rs = ps.executeQuery();

            List<Dish> dishes = new ArrayList<>();
            while (rs.next()) {
                dishes.add(new Dish(rs.getString("name"), rs.getString("ingredients")));
            }
            setDishes(dishes);
            return dishes;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
