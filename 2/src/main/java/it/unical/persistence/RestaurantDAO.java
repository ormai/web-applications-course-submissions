package it.unical.persistence;

import it.unical.model.Dish;
import it.unical.model.Restaurant;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Stateless static class
 */
public class RestaurantDAO {
    public static List<Restaurant> findAll() {
        try (PreparedStatement ps = DataSource.getConnection().prepareStatement("SELECT * FROM restaurant")) {
            return convert(ps.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Restaurant findById(String name) {
        try (PreparedStatement ps =
                     DataSource.getConnection().prepareStatement("SELECT * FROM restaurant WHERE name = ?")) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new RestaurantProxy(name, rs.getString("description"), rs.getString("location"));
            }
            return null; // not found
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save(Restaurant restaurant) {
        try (PreparedStatement ps = DataSource.getConnection().prepareStatement(
                "INSERT INTO restaurant VALUES (?, ?, ?)")) {
            ps.setString(1, restaurant.getName());
            ps.setString(2, restaurant.getDescription());
            ps.setString(3, restaurant.getLocation());
            ps.executeUpdate();

            // removeRelationship(restaurant.getName());
            for (Dish dish : restaurant.getDishes()) {
                if (DishDAO.findById(dish.getName()) == null) {
                    DishDAO.save(dish);
                }
                addRelationship(restaurant.getName(), dish.getName());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void delete(Restaurant restaurant) {
        try (PreparedStatement ps = DataSource.getConnection().prepareStatement(
                "DELETE FROM restaurant WHERE name = ?")) {
            ps.setString(1, restaurant.getName());
            ps.executeUpdate();

            for (Dish dish : restaurant.getDishes()) {
                dish.getRestaurants().remove(restaurant);
                removeRelationship(restaurant.getName());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Restaurant> findByDish(String dishName) {
        try (PreparedStatement ps = DataSource.getConnection().prepareStatement("""
                SELECT restaurant.name, restaurant.description, restaurant.location
                FROM dish
                    INNER JOIN restaurant_dish rd ON dish.name = rd.dish
                    INNER JOIN restaurant ON rd.restaurant = restaurant.name
                WHERE dish.name = ?
                """)) {
            ps.setString(1, dishName);
            return convert(ps.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void removeRelationship(String restaurantName) {
        try (PreparedStatement ps = DataSource.getConnection().prepareStatement(
                "DELETE FROM restaurant_dish WHERE restaurant = ?")) {
            ps.setString(1, restaurantName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Restaurant> convert(ResultSet rs) throws SQLException {
        List<Restaurant> restaurants = new ArrayList<>();
        while (rs.next()) {
            restaurants.add(new RestaurantProxy(
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("location")));
        }
        return restaurants;
    }

    /**
     * This is used by both {@link Dish} and this Data Access Object
     */
    static void addRelationship(String restaurant, String dish) {
        try (PreparedStatement ps = DataSource.getConnection().prepareStatement(
                "INSERT INTO restaurant_dish VALUES (?, ?)")) {
            ps.setString(1, restaurant);
            ps.setString(2, dish);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
