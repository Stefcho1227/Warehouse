package org.example.warehouse.dtos.in;

import org.example.warehouse.enums.Unit;

import java.math.BigDecimal;

public record PatchTransactionInDTO(
        BigDecimal quantity,
        Unit unit,
        BigDecimal unitPrice,
        String itemName,
        String warehouseName
) { }
