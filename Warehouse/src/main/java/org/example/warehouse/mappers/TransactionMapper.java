package org.example.warehouse.mappers;

import org.example.warehouse.dtos.out.TransactionOutDTO;
import org.example.warehouse.models.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    public TransactionOutDTO toDto(Transaction tx) {
        return new TransactionOutDTO(
                tx.getId(),
                tx.getItemName(),
                tx.getQuantity(),
                tx.getUnit(),
                tx.getUnitPrice(),
                tx.getWarehouseName(),
                tx.getCreatedAt()
        );
    }
}
