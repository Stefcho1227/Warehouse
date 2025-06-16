package org.example.warehouse.dtos.out;

import org.example.warehouse.enums.Unit;

import java.math.BigDecimal;

public record InventoryOutDTO(
        Long id,
        String itemName,
        BigDecimal quantity,
        Unit unit,
        BigDecimal unitPrice,
        String warehouseName)
{}
