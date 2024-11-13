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
public class DishDAO {
    public static List<Dish> findAll() {
        try (PreparedStatement ps = DataSource.getConnection().prepareStatement("SELECT * FROM dish")) {
            return convert(ps.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Dish findById(String name) {
        try (PreparedStatement ps = DataSource.getConnection().prepareStatement("SELECT * FROM dish WHERE name = ?")) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new DishProxy(name, rs.getString("ingredients"));
            }
            return null; // not found
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save(Dish dish) {
        try (PreparedStatement ps = DataSource.getConnection().prepareStatement("INSERT INTO dish VALUES (?, ?)")) {
            ps.setString(1, dish.getName());
            ps.setString(2, dish.getIngredients());
            ps.executeUpdate();

            for (Restaurant restaurant : dish.getRestaurants()) {
                if (RestaurantDAO.findById(restaurant.getName()) == null) {
                    RestaurantDAO.save(restaurant);
                }
                RestaurantDAO.addRelationship(restaurant.getName(), dish.getName());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void delete(Dish dish) {
        try (PreparedStatement ps = DataSource.getConnection().prepareStatement("DELETE FROM dish WHERE name = ?")) {
            ps.setString(1, dish.getName());
            ps.executeUpdate();

            for (Restaurant restaurant : dish.getRestaurants()) {
                restaurant.getDishes().remove(dish);
                removeRelationship(dish.getName());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Dish> findByRestaurant(String restaurantName) {
        try (PreparedStatement ps = DataSource.getConnection().prepareStatement("""
                SELECT dish.name, dish.ingredients
                FROM dish
                    INNER JOIN restaurant_dish rd ON dish.name = rd.dish
                    INNER JOIN restaurant ON rd.restaurant = restaurant.name
                WHERE restaurant.name = ?
                """)) {
            ps.setString(1, restaurantName);
            return convert(ps.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Dish> convert(ResultSet rs) throws SQLException {
        List<Dish> dishes = new ArrayList<>();
        while (rs.next()) {
            dishes.add(new DishProxy(rs.getString("name"), rs.getString("ingredients")));
        }
        return dishes;
    }

    private static void removeRelationship(String dishName) {
        try (PreparedStatement ps = DataSource.getConnection().prepareStatement(
                "DELETE FROM restaurant_dish WHERE dish = ?")) {
            ps.setString(1, dishName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
