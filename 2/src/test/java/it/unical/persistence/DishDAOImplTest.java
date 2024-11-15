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

public class DishDAOImplTest {
    private static final DishDAO dao = new DishDAOImpl();
    private static final Dish LINGUINE = new Dish("Linguine allo scoglio", "Linguine, frutti di mare, vino bianco");
    private static final Restaurant DA_MARIO = new Restaurant("Pizzeria da Mario", "", "Corso Mazzini, Cosenza");

    @BeforeEach
    void prepare() {
        DataSource.initDatabase();
    }

    @Test
    void findAllReturnsAllTheDishes() {
        List<Dish> emptyQuery = dao.findAll();
        assertEquals(emptyQuery.size(), 0);

        dao.create(LINGUINE);
        List<Dish> query = dao.findAll();
        assertEquals(query.size(), 1);
        assertEquals(query.getFirst(), LINGUINE);
        System.out.println(query.getFirst().toString());
    }

    @Test
    void findByIdReturnsADishGivenItsPrimaryKey() {
        assertNull(dao.findById("NonexistentPrimaryKey"));

        dao.create(LINGUINE);
        assertNotNull(dao.findById(LINGUINE.getName()));
        assertEquals(dao.findById(LINGUINE.getName()), LINGUINE);
    }

    @Test
    void createSavesADish() {
        assertNull(dao.findById(LINGUINE.getName())); // Pre-condition
        assertEquals(0, dao.findAll().size());
        dao.create(LINGUINE);
        assertNotNull(dao.findById(LINGUINE.getName())); // Post-condition
        assertEquals(1, dao.findAll().size());

        assertEquals(dao.findById(LINGUINE.getName()), LINGUINE);
        assertNotSame(dao.findById(LINGUINE.getName()), LINGUINE); // Equals, but different identities

        assertThrows(RuntimeException.class, () -> dao.create(LINGUINE)); // Try to add again the same primary key
    }

    @Test
    void updateWorks() {
        dao.create(LINGUINE);
        dao.update(new Dish(LINGUINE.getName(), "pizza", List.of(DA_MARIO)));

        Dish updated = dao.findById(LINGUINE.getName());
        assertEquals("pizza", updated.getIngredients());

        assertEquals(LINGUINE.getName(), dao.findByRestaurant(DA_MARIO.getName()).getFirst().getName());
    }

    @Test
    void deleteDeletesADish() {
        dao.create(LINGUINE);
        assertNotNull(dao.findById(LINGUINE.getName())); // Pre-condition
        assertEquals(1, dao.findAll().size());
        dao.delete(LINGUINE);
        assertNull(dao.findById(LINGUINE.getName())); // Post-condition
        assertEquals(dao.findAll().size(), 0);
    }

    @Test
    void findByRestaurantReturnsAllTheDishesAssociatedWithTheGivenRestaurant() {
        Dish linguine = new Dish(LINGUINE);
        linguine.getRestaurants().add(DA_MARIO);
        dao.create(linguine);

        List<Dish> dishes = dao.findByRestaurant(DA_MARIO.getName());
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
