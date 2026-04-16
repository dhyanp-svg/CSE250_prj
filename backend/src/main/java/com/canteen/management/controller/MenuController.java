package com.canteen.management.controller;

import com.canteen.management.dto.MenuItemRequest;
import com.canteen.management.dto.MenuItemResponse;
import com.canteen.management.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/api/menu")
    public List<MenuItemResponse> getAvailableMenu() {
        return menuService.getAvailableMenu();
    }

    @GetMapping("/api/admin/menu")
    public List<MenuItemResponse> getAllMenuItems() {
        return menuService.getAllMenuItems();
    }

    @PostMapping("/api/admin/menu")
    public MenuItemResponse createMenuItem(@Valid @RequestBody MenuItemRequest request) {
        return menuService.create(request);
    }

    @PutMapping("/api/admin/menu/{itemId}")
    public MenuItemResponse updateMenuItem(@PathVariable Long itemId, @Valid @RequestBody MenuItemRequest request) {
        return menuService.update(itemId, request);
    }

    @DeleteMapping("/api/admin/menu/{itemId}")
    public void deleteMenuItem(@PathVariable Long itemId) {
        menuService.delete(itemId);
    }
}
