package edu.ph.cvsu.imus.menu.exceptions;

public class MenuItemNotFoundException extends RuntimeException {

    public MenuItemNotFoundException(String id) {
        super("Menu item not found with id: " + id);
    }
}
