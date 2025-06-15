package org.example.warehouse.repository;

import org.example.warehouse.models.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
@Repository
public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    Optional<InventoryItem> findByItemNameAndWarehouseNameAndUnitPrice(String itemName, String warehouseName,
                                                                       BigDecimal unitPrice);
}
