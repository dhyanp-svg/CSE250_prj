package com.canteen.management.service;

import com.canteen.management.dto.MenuItemRequest;
import com.canteen.management.dto.MenuItemResponse;
import com.canteen.management.entity.MenuItem;
import com.canteen.management.exception.ApiException;
import com.canteen.management.repository.MenuItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {

    private final MenuItemRepository menuItemRepository;
    private final MapperService mapperService;

    public MenuService(MenuItemRepository menuItemRepository, MapperService mapperService) {
        this.menuItemRepository = menuItemRepository;
        this.mapperService = mapperService;
    }

    public List<MenuItemResponse> getAvailableMenu() {
        return menuItemRepository.findByAvailableTrueOrderByItemNameAsc().stream()
                .map(mapperService::toMenuResponse)
                .toList();
    }

    public List<MenuItemResponse> getAllMenuItems() {
        return menuItemRepository.findAll().stream()
                .map(mapperService::toMenuResponse)
                .toList();
    }

    public MenuItemResponse create(MenuItemRequest request) {
        MenuItem item = new MenuItem();
        apply(item, request);
        return mapperService.toMenuResponse(menuItemRepository.save(item));
    }

    public MenuItemResponse update(Long itemId, MenuItemRequest request) {
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new ApiException("Menu item not found"));
        apply(item, request);
        return mapperService.toMenuResponse(menuItemRepository.save(item));
    }

    public void delete(Long itemId) {
        if (!menuItemRepository.existsById(itemId)) {
            throw new ApiException("Menu item not found");
        }
        menuItemRepository.deleteById(itemId);
    }

    private void apply(MenuItem item, MenuItemRequest request) {
        item.setItemName(request.itemName());
        item.setPrice(request.price());
        item.setCategory(request.category());
        item.setAvailable(request.available());
        item.setDescription(request.description());
    }
}
