package org.example.warehouse.service.contracts;

import org.example.warehouse.dtos.out.InventoryOutDTO;

import java.util.List;

public interface InventoryService {
    List<InventoryOutDTO> getAll();
    InventoryOutDTO getById(Long id);
}
