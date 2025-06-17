package org.example.warehouse.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.warehouse.dtos.out.InventoryOutDTO;
import org.example.warehouse.enums.Unit;
import org.example.warehouse.mappers.InventoryMapper;
import org.example.warehouse.models.InventoryItem;
import org.example.warehouse.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceImplTest {
    @Mock
    InventoryRepository inventoryRepository;
    @Mock
    InventoryMapper inventoryMapper;
    @InjectMocks
    InventoryServiceImpl service;
    InventoryItem item;
    InventoryOutDTO dto;
    @BeforeEach
    void setUp() {
        item = InventoryItem.builder()
                .id(7L)
                .itemName("Rice")
                .warehouseName("Central")
                .unitPrice(new BigDecimal("2.30"))
                .unit(Unit.KG)
                .quantity(new BigDecimal("42.000"))
                .build();

        dto = new InventoryOutDTO(
                item.getId(),
                item.getItemName(),
                item.getQuantity(),
                item.getUnit(),
                item.getUnitPrice(),
                item.getWarehouseName());
    }
    @Test
    void getAll_returnsMappedDtos() {
        when(inventoryRepository.findAll()).thenReturn(List.of(item));
        when(inventoryMapper.toDto(item)).thenReturn(dto);

        List<InventoryOutDTO> result = service.getAll();

        assertThat(result).containsExactly(dto);
        verify(inventoryRepository).findAll();
    }
    @Test
    void getById_existing_returnsDto() {
        when(inventoryRepository.findById(7L)).thenReturn(Optional.of(item));
        when(inventoryMapper.toDto(item)).thenReturn(dto);

        InventoryOutDTO result = service.getById(7L);

        assertThat(result).isEqualTo(dto);
        verify(inventoryRepository).findById(7L);
    }
    @Test
    void getById_missing_throws() {
        when(inventoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Transaction not found");

        verify(inventoryRepository).findById(99L);
    }

}
