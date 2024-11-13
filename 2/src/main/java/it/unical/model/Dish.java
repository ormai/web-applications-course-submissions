package it.unical.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class Dish {
    private String name, ingredients;
    private List<Restaurant> restaurants = new ArrayList<>();

    /**
     * Two argument - partial - constructor
     */
    public Dish(String name, String ingredients) {
        this.name = name;
        this.ingredients = ingredients;
    }

    /**
     * Copy constructor
     */
    public Dish(Dish dish) {
        name = dish.getName();
        ingredients = dish.getIngredients();
        restaurants.addAll(dish.getRestaurants());
    }
}
