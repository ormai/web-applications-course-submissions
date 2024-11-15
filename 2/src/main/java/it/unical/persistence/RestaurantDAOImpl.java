package it.unical.persistence;

import it.unical.model.Dish;
import it.unical.model.Restaurant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RestaurantDAOImpl implements RestaurantDAO {
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
    static void addRelationship(Restaurant restaurant, Dish dish) {
        Connection c = DataSource.getConnection();
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO restaurant_dish VALUES (?, ?)");
             PreparedStatement restIn = c.prepareStatement("SELECT COUNT(*) FROM restaurant WHERE name = ?");
             PreparedStatement dishIn = c.prepareStatement("SELECT COUNT(*) FROM dish WHERE name = ?")) {

            restIn.setString(1, restaurant.getName());
            ResultSet rs = restIn.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                new RestaurantDAOImpl().create(restaurant);
            }

            dishIn.setString(1, dish.getName());
            ResultSet rsDish = dishIn.executeQuery();
            if (rsDish.next() && rsDish.getInt(1) == 0) {
                new DishDAOImpl().create(dish);
            }

            ps.setString(1, restaurant.getName());
            ps.setString(2, dish.getName());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Restaurant> findAll() {
        try (PreparedStatement ps = DataSource.getConnection().prepareStatement("SELECT * FROM restaurant")) {
            return convert(ps.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Restaurant findById(String name) {
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

    @Override
    public void update(Restaurant restaurant) {
        try (PreparedStatement ps = DataSource.getConnection().prepareStatement(
                "UPDATE restaurant SET description = ?, location = ? WHERE name = ?")) {
            ps.setString(1, restaurant.getDescription());
            ps.setString(2, restaurant.getLocation());
            ps.setString(3, restaurant.getName());
            ps.executeUpdate();

            for (Dish dish : restaurant.getDishes()) {
                addRelationship(restaurant, dish);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(Restaurant restaurant) {
        try (PreparedStatement ps = DataSource.getConnection().prepareStatement(
                "INSERT INTO restaurant VALUES (?, ?, ?)")) {
            ps.setString(1, restaurant.getName());
            ps.setString(2, restaurant.getDescription());
            ps.setString(3, restaurant.getLocation());
            ps.executeUpdate();

            DishDAO dishDAO = new DishDAOImpl();
            for (Dish dish : restaurant.getDishes()) {
                if (dishDAO.findById(dish.getName()) == null) {
                    dishDAO.create(dish);
                }
                addRelationship(restaurant, dish);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Restaurant restaurant) {
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

    public List<Restaurant> findByDish(String dishName) {
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
}
