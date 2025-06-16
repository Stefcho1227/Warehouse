package org.example.warehouse.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.warehouse.dtos.out.InventoryOutDTO;
import org.example.warehouse.mappers.InventoryMapper;
import org.example.warehouse.models.InventoryItem;
import org.example.warehouse.repository.InventoryRepository;
import org.example.warehouse.service.contracts.InventoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    public InventoryServiceImpl(InventoryRepository inventoryRepository, InventoryMapper inventoryMapper) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryMapper = inventoryMapper;
    }

    @Override
    public List<InventoryOutDTO> getAll() {
        return inventoryRepository.findAll().stream()
                .map(inventoryMapper::toDto)
                .toList();
    }

    @Override
    public InventoryOutDTO getById(Long id) {
        InventoryItem inventoryItem = inventoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
        return inventoryMapper.toDto(inventoryItem);
    }
}
