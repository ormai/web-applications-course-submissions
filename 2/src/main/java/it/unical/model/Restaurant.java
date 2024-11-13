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
public class Restaurant {
    private String name, description, location;
    private List<Dish> dishes = new ArrayList<>();

    /**
     * Three argument - partial - constructor
     */
    public Restaurant(String name, String description, String location) {
        this.name = name;
        this.description = description;
        this.location = location;
    }

    /**
     * Copy constructor
     */
    public Restaurant(Restaurant restaurant) { name = restaurant.getName();
        description = restaurant.getDescription();
        location = restaurant.getLocation();
        dishes.addAll(restaurant.getDishes());
    }
}
