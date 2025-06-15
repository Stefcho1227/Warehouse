package org.example.warehouse.dtos.out;

import org.example.warehouse.enums.Unit;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransactionOutDTO(
        Long id,
        String itemName,
        BigDecimal quantity,
        Unit unit,
        BigDecimal unitPrice,
        String warehouseName,
        OffsetDateTime createdAt)
{}