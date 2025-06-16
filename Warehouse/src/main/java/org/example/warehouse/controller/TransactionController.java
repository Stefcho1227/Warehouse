package org.example.warehouse.controller;

import jakarta.validation.Valid;
import org.example.warehouse.dtos.in.PatchTransactionInDTO;
import org.example.warehouse.dtos.in.TransactionInDTO;
import org.example.warehouse.dtos.out.TransactionOutDTO;
import org.example.warehouse.service.contracts.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/transactions")
public class TransactionController {
    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }
    @GetMapping
    public ResponseEntity<List<TransactionOutDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<TransactionOutDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
    @PostMapping
    public ResponseEntity<List<TransactionOutDTO>> create(@RequestBody @Valid List<TransactionInDTO> transactions) {
        return ResponseEntity.ok(service.createTransactions(transactions));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<List<TransactionOutDTO>> patch(@PathVariable Long id,
                                            @RequestBody @Valid PatchTransactionInDTO transaction) {
        return ResponseEntity.ok(service.patchTransaction(id, transaction));
    }
}
