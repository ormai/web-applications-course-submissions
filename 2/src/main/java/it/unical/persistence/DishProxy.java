package it.unical.persistence;

import it.unical.model.Dish;
import it.unical.model.Restaurant;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DishProxy extends Dish {
    public DishProxy(String name, String ingredients) {
        super(name, ingredients);
    }

    public DishProxy(Dish dish) {
        super(dish.getName(), dish.getIngredients(), dish.getRestaurants());
    }

    @Override
    public List<Restaurant> getRestaurants() {
        try (PreparedStatement ps = DataSource.getConnection().prepareStatement("""
                SELECT restaurant.name, restaurant.description, restaurant.location
                FROM dish
                    INNER JOIN restaurant_dish rd ON rd.dish = dish.name
                    INNER JOIN restaurant on rd.restaurant = restaurant.name
                """)) {
            ResultSet rs = ps.executeQuery();

            List<Restaurant> restaurants = new ArrayList<>();
            while (rs.next()) {
                restaurants.add(new Restaurant(
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("location")));
            }
            setRestaurants(restaurants);
            return restaurants;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
