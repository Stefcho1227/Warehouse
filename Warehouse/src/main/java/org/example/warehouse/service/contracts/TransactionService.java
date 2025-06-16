package org.example.warehouse.service.contracts;

import org.example.warehouse.dtos.in.PatchTransactionInDTO;
import org.example.warehouse.dtos.in.TransactionInDTO;
import org.example.warehouse.dtos.out.TransactionOutDTO;

import java.util.List;

public interface TransactionService {
    List<TransactionOutDTO> getAll();
    TransactionOutDTO getById(Long id);
    List<TransactionOutDTO> createTransactions(List<TransactionInDTO> requests);

    List<TransactionOutDTO> patchTransaction(Long originalId, PatchTransactionInDTO patch);
}
