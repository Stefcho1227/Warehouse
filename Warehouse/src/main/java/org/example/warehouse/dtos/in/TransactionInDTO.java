package org.example.warehouse.dtos.in;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.warehouse.enums.Unit;

import java.math.BigDecimal;

public record TransactionInDTO (
    @NotBlank
    @Size(max = 90)
    String itemName,
    @NotNull
    @Digits(integer = 14, fraction = 3)
    BigDecimal quantity,
    @NotNull
    Unit unit,
    @NotNull
    @Digits(integer = 8, fraction = 2)
    BigDecimal unitPrice,
    @NotBlank
    @Size(max = 90)
    String warehouseName)
{}

