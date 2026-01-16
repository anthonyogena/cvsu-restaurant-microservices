package edu.ph.cvsu.imus.menu.service;

import edu.ph.cvsu.imus.menu.model.MenuItem;
import edu.ph.cvsu.imus.menu.model.enums.MenuCategory;

import java.util.List;



public interface MenuService {

    MenuItem createMenuItem(MenuItem menuItem);

    MenuItem updateMenuItem(String id, MenuItem menuItem);

    MenuItem getMenuItemById(String id);

    MenuItem getMenuItemByName(String name);

    List<MenuItem> getAllMenuItems();

    List<MenuItem> getMenuItemsByCategory(MenuCategory category);

    List<MenuItem> getAvailableMenuItems();

    void deleteMenuItem(String id);
}
