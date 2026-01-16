package edu.ph.cvsu.imus.menu.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MenuCategory {

    APPETIZER("APPETIZER"),
    MAIN_COURSE("MAIN_COURSE"),
    DESSERT("DESSERT"),
    DRINK("DRINK");

    private final String value;

    MenuCategory(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static MenuCategory fromValue(String value) {
        for (MenuCategory category : MenuCategory.values()) {
            if (category.value.equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid menu category: " + value);
    }
}