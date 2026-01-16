package edu.ph.cvsu.imus.menu.service.impl;

import edu.ph.cvsu.imus.menu.exceptions.MenuItemDuplicateEntryException;
import edu.ph.cvsu.imus.menu.exceptions.MenuItemNotFoundException;
import edu.ph.cvsu.imus.menu.model.MenuItem;
import edu.ph.cvsu.imus.menu.model.enums.MenuCategory;
import edu.ph.cvsu.imus.menu.repository.MenuItemRepository;
import edu.ph.cvsu.imus.menu.service.MenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MenuServiceImpl implements MenuService {

    private final MenuItemRepository menuItemRepository;

    public MenuServiceImpl(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    /* ---------------- Create ---------------- */

    @Override
    public MenuItem createMenuItem(MenuItem menuItem)
    {
        if (menuItemRepository.findMenuItemByName(menuItem.getName()).isPresent()){
            throw new MenuItemDuplicateEntryException(menuItem.getName());

        }
        return menuItemRepository.save(menuItem);
    }

    /* ---------------- Read ---------------- */

    @Override
    @Transactional(readOnly = true)
    public MenuItem getMenuItemById(String id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new MenuItemNotFoundException(id));
    }

    @Override
    public MenuItem getMenuItemByName(String name) {
        return menuItemRepository.findMenuItemByName(name)
                .orElseThrow(() -> new MenuItemNotFoundException(name));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItem> getMenuItemsByCategory(MenuCategory category) {
        return menuItemRepository.findByCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItem> getAvailableMenuItems() {
        return menuItemRepository.findByAvailableTrue();
    }

    /* ---------------- Update ---------------- */

    @Override
    public MenuItem updateMenuItem(String id, MenuItem updatedItem) {
        MenuItem existing = getMenuItemById(id);

        existing.setName(updatedItem.getName());
        existing.setDescription(updatedItem.getDescription());
        existing.setCategory(updatedItem.getCategory());
        existing.setPrice(updatedItem.getPrice());
        existing.setAvailable(updatedItem.isAvailable());

        return menuItemRepository.save(existing);
    }

    /* ---------------- Delete ---------------- */

    @Override
    public void deleteMenuItem(String id) {
        MenuItem existing = getMenuItemById(id);
        menuItemRepository.delete(existing);
    }
}
