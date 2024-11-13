package it.unical.persistence;

import it.unical.model.Dish;
import it.unical.model.Restaurant;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DishDAOTest {
    private static final Dish LINGUINE = new Dish("Linguine allo scoglio", "Linguine, frutti di mare, vino bianco");
    private static final Restaurant DA_MARIO = new Restaurant("Pizzeria da Mario", "", "Corso Mazzini, Cosenza");

    @BeforeEach
    void prepare() {
        DataSource.initDatabase();
    }

    @Test
    void findAllReturnsAllTheDishes() {
        List<Dish> emptyQuery = DishDAO.findAll();
        assertEquals(emptyQuery.size(), 0);

        DishDAO.save(LINGUINE);
        List<Dish> query = DishDAO.findAll();
        assertEquals(query.size(), 1);
        assertEquals(query.getFirst(), LINGUINE);
        System.out.println(query.getFirst().toString());
    }

    @Test
    void findByIdReturnsADishGivenItsPrimaryKey() {
        assertNull(DishDAO.findById("NonexistentPrimaryKey"));

        DishDAO.save(LINGUINE);
        assertNotNull(DishDAO.findById(LINGUINE.getName()));
        assertEquals(DishDAO.findById(LINGUINE.getName()), LINGUINE);
    }

    @Test
    void saveSavesADish() {
        assertNull(DishDAO.findById(LINGUINE.getName())); // Pre-condition
        assertEquals(DishDAO.findAll().size(), 0);
        DishDAO.save(LINGUINE);
        assertNotNull(DishDAO.findById(LINGUINE.getName())); // Post-condition
        assertEquals(DishDAO.findAll().size(), 1);

        assertEquals(DishDAO.findById(LINGUINE.getName()), LINGUINE);
        assertNotSame(DishDAO.findById(LINGUINE.getName()), LINGUINE); // Equals, but different identities

        assertThrows(RuntimeException.class, () -> DishDAO.save(LINGUINE)); // Try to add again the same primary key
    }

    @Test
    void deleteDeletesADish() {
        DishDAO.save(LINGUINE);
        assertNotNull(DishDAO.findById(LINGUINE.getName())); // Pre-condition
        assertEquals(DishDAO.findAll().size(), 1);
        DishDAO.delete(LINGUINE);
        assertNull(DishDAO.findById(LINGUINE.getName())); // Post-condition
        assertEquals(DishDAO.findAll().size(), 0);
    }

    @Test
    void findByRestaurantReturnsAllTheDishesAssociatedWithTheGivenRestaurant() {
        Dish linguine = new Dish(LINGUINE);
        linguine.getRestaurants().add(DA_MARIO);
        DishDAO.save(linguine);

        List<Dish> dishes = DishDAO.findByRestaurant(DA_MARIO.getName());
        assertEquals(1, dishes.size());
        assertEquals(linguine, dishes.getFirst());

        // Circular reference
        Dish dish = new RestaurantProxy(linguine.getRestaurants().getFirst()).getDishes().getFirst();
        assertEquals(dish, LINGUINE);

        // Two-level circular reference
        assertEquals(dish.getRestaurants().size(), 0); // Without the proxy id doesn't work

        Dish dishProxy = new DishProxy(dish);
        assertEquals(dishProxy.getRestaurants().size(), 1); // With the proxy it works

        assertEquals(dishProxy.getRestaurants().getFirst(), DA_MARIO); // Back to start

        // Linguine -> Pizzeria da Mario -> Linguine -> Pizzeria da Mario
    }
}
