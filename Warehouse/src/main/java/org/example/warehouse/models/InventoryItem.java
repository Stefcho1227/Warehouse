package org.example.warehouse.models;

import jakarta.persistence.*;
import lombok.*;
import org.example.warehouse.enums.Unit;

import java.math.BigDecimal;

@Entity
@Table(name = "inventory", uniqueConstraints =
@UniqueConstraint(columnNames = {"itemName", "warehouseName", "unitPrice"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 90)
    private String itemName;

    @Column(nullable = false, length = 90)
    private String warehouseName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Unit unit;

    @Column(nullable = false, precision = 14, scale = 3)
    private BigDecimal quantity;
}
