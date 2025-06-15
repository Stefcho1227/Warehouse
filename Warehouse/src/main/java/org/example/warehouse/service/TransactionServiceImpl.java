package org.example.warehouse.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.warehouse.dtos.in.PatchTransactionInDTO;
import org.example.warehouse.dtos.in.TransactionInDTO;
import org.example.warehouse.dtos.out.TransactionOutDTO;
import org.example.warehouse.mappers.TransactionMapper;
import org.example.warehouse.models.InventoryItem;
import org.example.warehouse.models.Transaction;
import org.example.warehouse.repository.InventoryRepository;
import org.example.warehouse.repository.TransactionRepository;
import org.example.warehouse.service.contracts.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final InventoryRepository inventoryRepository;
    private final TransactionMapper transactionMapper;

    public TransactionServiceImpl(TransactionRepository transactionRepository, InventoryRepository inventoryRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.inventoryRepository = inventoryRepository;
        this.transactionMapper = transactionMapper;
    }
    @Transactional
    public List<TransactionOutDTO> createTransactions(List<TransactionInDTO> request) {
        return request.stream()
                .map(this::applyNewTransaction)
                .map(transactionMapper::toDto)
                .toList();
    }
    @Transactional
    public List<TransactionOutDTO> patchTransaction(Long originalId, PatchTransactionInDTO fixTransaction) {
        Transaction original = transactionRepository.findById(originalId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
        Transaction compensation = transactionRepository.save(
                original.toBuilder()
                        .id(null)
                        .quantity(original.getQuantity().negate())
                        .createdAt(null)
                        .build());
        applyToInventory(compensation);
        Transaction corrected = transactionRepository.save(Transaction.builder()
                        .itemName(fixTransaction.itemName() != null ? fixTransaction.itemName() :
                                original.getItemName())
                        .quantity(fixTransaction.quantity() != null ? fixTransaction.quantity() :
                                original.getQuantity())
                        .unit(fixTransaction.unit() != null ? fixTransaction.unit() :
                                original.getUnit())
                        .unitPrice(fixTransaction.unitPrice() != null ? fixTransaction.unitPrice() :
                                original.getUnitPrice())
                        .warehouseName(fixTransaction.warehouseName() != null ? fixTransaction.warehouseName() :
                                original.getWarehouseName())
                        .build());
        applyToInventory(corrected);
        return List.of(transactionMapper.toDto(compensation), transactionMapper.toDto(corrected));
    }
    private Transaction applyNewTransaction(TransactionInDTO dto) {
        Transaction transaction = transactionRepository.save(Transaction.builder()
                .itemName(dto.itemName())
                .quantity(dto.quantity())
                .unit(dto.unit())
                .unitPrice(dto.unitPrice())
                .warehouseName(dto.warehouseName())
                .createdAt(OffsetDateTime.now())
                .build());
        applyToInventory(transaction);
        return transaction;
    }
    private void applyToInventory(Transaction transaction) {
        InventoryItem inventoryItem = inventoryRepository
                .findByItemNameAndWarehouseNameAndUnitPrice(
                        transaction.getItemName(), transaction.getWarehouseName(), transaction.getUnitPrice())
                .orElseGet(() -> InventoryItem.builder()
                        .itemName(transaction.getItemName())
                        .warehouseName(transaction.getWarehouseName())
                        .unitPrice(transaction.getUnitPrice())
                        .unit(transaction.getUnit())
                        .quantity(BigDecimal.ZERO)
                        .build());
        inventoryItem.setQuantity(inventoryItem.getQuantity().add(transaction.getQuantity()));
        inventoryRepository.save(inventoryItem);
    }
}
