package edu.ph.cvsu.imus.menu.repository;

import edu.ph.cvsu.imus.menu.model.MenuItem;
import edu.ph.cvsu.imus.menu.model.enums.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository extends JpaRepository<MenuItem, String> {

    @Query(value = "Select * from menu_items where name = ?1", nativeQuery = true )
    Optional<MenuItem> findMenuItemByName(String name);

    List<MenuItem> findByCategory(MenuCategory category);

    List<MenuItem> findByAvailableTrue();
}
