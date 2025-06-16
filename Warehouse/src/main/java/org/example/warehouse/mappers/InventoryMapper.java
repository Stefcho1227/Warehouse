package org.example.warehouse.mappers;

import org.example.warehouse.dtos.out.InventoryOutDTO;
import org.example.warehouse.models.InventoryItem;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {
    public InventoryOutDTO toDto(InventoryItem inventoryItem) {
        return new InventoryOutDTO(
                inventoryItem.getId(),
                inventoryItem.getItemName(),
                inventoryItem.getQuantity(),
                inventoryItem.getUnit(),
                inventoryItem.getUnitPrice(),
                inventoryItem.getWarehouseName()
        );
    }
}
