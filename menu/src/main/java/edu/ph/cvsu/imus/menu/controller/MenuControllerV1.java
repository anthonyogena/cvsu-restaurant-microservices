package edu.ph.cvsu.imus.menu.controller;


import edu.ph.cvsu.imus.menu.model.MenuItem;
import edu.ph.cvsu.imus.menu.model.enums.MenuCategory;
import edu.ph.cvsu.imus.menu.service.MenuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@RestController
@RequestMapping("/v1/menu")
public class MenuControllerV1 {

    private final MenuService menuService;

    public MenuControllerV1(MenuService menuService) {
        this.menuService = menuService;
    }

    /* ---------------- CREATE ---------------- */

    @PostMapping("/items")
    public ResponseEntity<MenuItem> createMenuItem(
            @RequestBody MenuItem menuItem) {

        MenuItem created = menuService.createMenuItem(menuItem);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    /* ---------------- READ ---------------- */

    @GetMapping("/items/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(
            @PathVariable String id) {

        return ResponseEntity.ok(menuService.getMenuItemById(id));
    }
    @GetMapping("/items/name/{name}")
    public ResponseEntity<MenuItem> getMenuItemByName(
            @PathVariable String name) {

        return ResponseEntity.ok(menuService.getMenuItemByName(name));
    }

    @GetMapping("/items")
    public ResponseEntity<List<MenuItem>> getMenuItems(
            @RequestParam(required = false) MenuCategory category,
            @RequestParam(required = false, defaultValue = "false") boolean availableOnly) {

        if (availableOnly) {
            return ResponseEntity.ok(menuService.getAvailableMenuItems());
        }

        if (category != null) {
            return ResponseEntity.ok(menuService.getMenuItemsByCategory(category));
        }

        return ResponseEntity.ok(menuService.getAllMenuItems());
    }

    /* ---------------- UPDATE ---------------- */

    @PutMapping("/items/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(
            @PathVariable String id,
            @RequestBody MenuItem menuItem) {

        return ResponseEntity.ok(menuService.updateMenuItem(id, menuItem));
    }

    /* ---------------- DELETE ---------------- */

    @DeleteMapping("/items/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenuItem(@PathVariable String id) {
        menuService.deleteMenuItem(id);
    }
}
