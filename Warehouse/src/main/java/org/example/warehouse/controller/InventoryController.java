package org.example.warehouse.controller;

import org.example.warehouse.dtos.out.InventoryOutDTO;
import org.example.warehouse.service.contracts.InventoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/inventories")
public class InventoryController {
    private final InventoryService inventoryService;
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    @GetMapping
    public List<InventoryOutDTO> getAll() {
        return inventoryService.getAll();
    }
    @GetMapping("/{id}")
    public InventoryOutDTO getById(@PathVariable Long id) {
        return inventoryService.getById(id);
    }
}
