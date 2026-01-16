package edu.ph.cvsu.imus.menu.exceptions;

public class MenuItemDuplicateEntryException extends RuntimeException {

    public MenuItemDuplicateEntryException(String name) {
        super("Menu Item is already in the system: " + name);
    }
}
