package org.example.warehouse.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.warehouse.dtos.in.PatchTransactionInDTO;
import org.example.warehouse.dtos.in.TransactionInDTO;
import org.example.warehouse.dtos.out.TransactionOutDTO;
import org.example.warehouse.enums.Unit;
import org.example.warehouse.mappers.TransactionMapper;
import org.example.warehouse.models.InventoryItem;
import org.example.warehouse.models.Transaction;
import org.example.warehouse.repository.InventoryRepository;
import org.example.warehouse.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {
    @Mock
    TransactionRepository transactionRepository;
    @Mock
    InventoryRepository inventoryRepository;
    @Mock
    TransactionMapper transactionMapper;
    @InjectMocks
    TransactionServiceImpl service;
    Transaction transaction;
    TransactionOutDTO transactionOutDTO;
    InventoryItem inventoryItem;
    @BeforeEach
    void setUp() {
        transaction = Transaction.builder()
                .id(1L)
                .itemName("Rice")
                .quantity(new BigDecimal("10.000"))
                .unit(Unit.KG)
                .unitPrice(new BigDecimal("2.30"))
                .warehouseName("Central")
                .createdAt(OffsetDateTime.now())
                .build();

        transactionOutDTO = new TransactionOutDTO(
                transaction.getId(), transaction.getItemName(), transaction.getQuantity(), transaction.getUnit(),
                transaction.getUnitPrice(), transaction.getWarehouseName(), transaction.getCreatedAt());

        inventoryItem = InventoryItem.builder()
                .id(5L)
                .itemName(transaction.getItemName())
                .warehouseName(transaction.getWarehouseName())
                .unitPrice(transaction.getUnitPrice())
                .unit(transaction.getUnit())
                .quantity(BigDecimal.ZERO)
                .build();
    }
    @Test
    void getAll_returnsMappedDtos() {
        when(transactionRepository.findAll()).thenReturn(List.of(transaction));
        when(transactionMapper.toDto(transaction)).thenReturn(transactionOutDTO);
        List<TransactionOutDTO> result = service.getAll();
        assertThat(result).containsExactly(transactionOutDTO);
        verify(transactionRepository).findAll();
    }
    @Test
    void getById_existing_returnsDto() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transactionMapper.toDto(transaction)).thenReturn(transactionOutDTO);
        TransactionOutDTO result = service.getById(1L);
        assertThat(result).isEqualTo(transactionOutDTO);
        verify(transactionRepository).findById(1L);
    }
    @Test
    void getById_missing_throws() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Transaction not found");
    }
    @Test
    void createTransactions_persistsAndUpdatesInventory() {
        TransactionInDTO inDTO = new TransactionInDTO(
                transaction.getItemName(), transaction.getQuantity(), transaction.getUnit(),
                transaction.getUnitPrice(), transaction.getWarehouseName());
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> {
                    Transaction saved = invocation.getArgument(0);
                    saved.setId(42L);
                    return saved;
                });
        when(inventoryRepository.findByItemNameAndWarehouseNameAndUnitPrice(
                anyString(), anyString(), any(BigDecimal.class)))
                .thenReturn(Optional.of(inventoryItem));
        when(transactionMapper.toDto(any(Transaction.class)))
                .thenReturn(transactionOutDTO);
        List<TransactionOutDTO> result = service.createTransactions(List.of(inDTO));
        assertThat(result).hasSize(1);
        verify(transactionRepository).save(any(Transaction.class));
        verify(inventoryRepository).save(invCaptor.capture());
        assertThat(invCaptor.getValue().getQuantity())
                .isEqualByComparingTo(new BigDecimal("10.000"));
    }
    @Captor
    ArgumentCaptor<InventoryItem> invCaptor;
    @Test
    void patchTransaction_createsCompensationAndCorrection() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(new Answer<>() {
                    long idSeq = 100;
                    @Override public Object answer(InvocationOnMock i) {
                        Transaction t = i.getArgument(0);
                        t.setId(idSeq++);
                        return t;
                    }
                });
        PatchTransactionInDTO patch = new PatchTransactionInDTO(
                null,null, new BigDecimal("8.000"), null, null);
        when(transactionMapper.toDto(any(Transaction.class))).thenReturn(transactionOutDTO);
        List<TransactionOutDTO> list = service.patchTransaction(1L, patch);
        assertThat(list).hasSize(2);
        verify(transactionRepository, times(2)).save(any(Transaction.class));
        verify(inventoryRepository, times(2)).save(any(InventoryItem.class));
        assertThat(transaction.isCorrected()).isTrue();
    }
    @Test
    void patchTransaction_noChanges_createsMirrorCorrection() {
        when(transactionRepository.findById(1L))
                .thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(new Answer<>() {
                    long idSeq = 10;
                    @Override public Transaction answer(InvocationOnMock i) {
                        Transaction t = i.getArgument(0);
                        t.setId(idSeq++);
                        return t;
                    }
                });
        PatchTransactionInDTO patch = new PatchTransactionInDTO(
                null, null, null, null, null);
        when(transactionMapper.toDto(any(Transaction.class))).thenReturn(transactionOutDTO);
        service.patchTransaction(1L, patch);
        ArgumentCaptor<Transaction> txCap = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(2)).save(txCap.capture());
        Transaction compensation = txCap.getAllValues().get(0);
        Transaction corrected    = txCap.getAllValues().get(1);
        assertThat(transaction.isCorrected()).isTrue();
        assertThat(compensation.getQuantity()).isEqualByComparingTo(transaction.getQuantity().negate());
        assertThat(corrected.getItemName()).isEqualTo(transaction.getItemName());
        assertThat(corrected.getQuantity()).isEqualByComparingTo(transaction.getQuantity());
        assertThat(corrected.getUnit()).isEqualTo(transaction.getUnit());
        assertThat(corrected.getUnitPrice()).isEqualByComparingTo(transaction.getUnitPrice());
        assertThat(corrected.getWarehouseName()).isEqualTo(transaction.getWarehouseName());
    }
    @Test
    void patchTransaction_alreadyCorrected_throws() {
        transaction.setCorrected(true);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        assertThatThrownBy(() ->
                service.patchTransaction(1L, Mockito.mock(PatchTransactionInDTO.class)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already been corrected");
    }
    @Test
    void patchTransaction_missingOriginal_throws() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.patchTransaction(1L, Mockito.mock(PatchTransactionInDTO.class)))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
