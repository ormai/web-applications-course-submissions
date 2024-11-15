package it.unical.persistence;

import it.unical.model.Dish;
import it.unical.model.Restaurant;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RestaurantDAOImplTest {
    private static final RestaurantDAO dao = new RestaurantDAOImpl();
    private static final Restaurant DA_MARIO = new Restaurant("Pizzeria da Mario", "", "Corso Mazzini, Cosenza");
    private static final Dish LINGUINE = new Dish("Linguine allo scoglio", "Linguine, frutti di mare, vino bianco");

    @BeforeEach
    void prepare() {
        DataSource.initDatabase();
    }

    @Test
    void findAllReturnsAllTheDishes() {
        List<Restaurant> emptyQuery = dao.findAll();
        assertEquals(emptyQuery.size(), 0);

        dao.create(DA_MARIO);
        List<Restaurant> query = dao.findAll();
        assertEquals(query.size(), 1);
        assertEquals(query.getFirst(), DA_MARIO);
        System.out.println(query.getFirst().toString());
    }

    @Test
    void findByIdReturnsADishGivenItsPrimaryKey() {
        assertNull(dao.findById("NonexistentPrimaryKey"));

        dao.create(DA_MARIO);
        assertNotNull(dao.findById(DA_MARIO.getName()));
        assertEquals(dao.findById(DA_MARIO.getName()), DA_MARIO);
    }

    @Test
    void createSavesADish() {
        assertNull(dao.findById(DA_MARIO.getName())); // Pre-condition
        assertEquals(dao.findAll().size(), 0);
        dao.create(DA_MARIO);
        assertNotNull(dao.findById(DA_MARIO.getName())); // Post-condition
        assertEquals(dao.findAll().size(), 1);
        assertEquals(dao.findById(DA_MARIO.getName()), DA_MARIO);
        assertNotSame(dao.findById(DA_MARIO.getName()), DA_MARIO); // Equals, but different identities
    }

    @Test
    void updateWorks() {
        dao.create(DA_MARIO);
        dao.update(new Restaurant(DA_MARIO.getName(), DA_MARIO.getDescription(), "Burundi", List.of(LINGUINE)));

        Restaurant updated = dao.findById(DA_MARIO.getName());
        assertEquals("Burundi", updated.getLocation());

        assertEquals(DA_MARIO.getName(), dao.findByDish(LINGUINE.getName()).getFirst().getName());
    }

    @Test
    void deleteDeletesADish() {
        dao.create(DA_MARIO);
        assertNotNull(dao.findById(DA_MARIO.getName())); // Pre-condition
        assertEquals(dao.findAll().size(), 1);
        dao.delete(DA_MARIO);
        assertNull(dao.findById(DA_MARIO.getName())); // Post-condition
        assertEquals(dao.findAll().size(), 0);
    }

    @Test
    void findByRestaurantReturnsAllTheDishesAssociatedWithTheGivenRestaurant() {
        Restaurant daMario = new Restaurant(DA_MARIO);
        daMario.getDishes().add(LINGUINE);
        dao.create(daMario);

        List<Restaurant> restaurants = dao.findByDish(LINGUINE.getName());
        assertEquals(1, restaurants.size());
        assertEquals(daMario, restaurants.getFirst());

        // Circular reference
        Restaurant restaurant = new DishProxy(daMario.getDishes().getFirst()).getRestaurants().getFirst();
        assertEquals(restaurant, DA_MARIO);

        // Two-level circular reference
        assertEquals(restaurant.getDishes().size(), 0); // Without the proxy id doesn't work

        Restaurant restaurantProxy = new RestaurantProxy(restaurant);
        assertEquals(restaurantProxy.getDishes().size(), 1); // With the proxy it works

        assertEquals(restaurantProxy.getDishes().getFirst(), LINGUINE);  // Back to start

        // Pizzeria da Mario -> Linguine -> Pizzeria da Mario -> Linguine
    }
}
