package it.unical.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Wrapper of {@code DriverManager.getConnection()} that adds lazy loading and error handling.
 */
public class DataSource {
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(
                        "jdbc:postgresql://localhost:5432/dao_exercise", "postgres", ""
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            initDatabase();
        }
        return connection;
    }

    /**
     * Bootstrap the basic relational scheme
     */
    public static void initDatabase() {
        try (PreparedStatement ps = getConnection().prepareStatement("""
                DROP TABLE IF EXISTS restaurant_dish, dish, restaurant;
                
                CREATE TABLE dish (name VARCHAR PRIMARY KEY, ingredients VARCHAR);
                
                CREATE TABLE restaurant (name VARCHAR PRIMARY KEY, description VARCHAR, location VARCHAR);
                
                CREATE TABLE restaurant_dish (
                    restaurant VARCHAR,
                    dish VARCHAR,
                    PRIMARY KEY (restaurant, dish),
                    FOREIGN KEY (restaurant) REFERENCES restaurant(name),
                    FOREIGN KEY (dish) REFERENCES dish(name)
                );
                """)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
