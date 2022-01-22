package bootcamp.com.transactionms.business.impl;

import static org.mockito.Mockito.when;

import bootcamp.com.transactionms.business.helper.FilterTransaction;
import bootcamp.com.transactionms.business.helper.FilterTransactionCredit;
import bootcamp.com.transactionms.business.helper.FilterTransactionDebit;
import bootcamp.com.transactionms.business.helper.WebClientProductHelper;
import bootcamp.com.transactionms.model.ProductDto;
import bootcamp.com.transactionms.model.Transaction;
import bootcamp.com.transactionms.model.TransactionDto;
import bootcamp.com.transactionms.repository.ITransactionRepository;
import bootcamp.com.transactionms.utils.ConstantsCredit;
import bootcamp.com.transactionms.utils.ConstantsCreditTransac;
import bootcamp.com.transactionms.utils.ConstantsDebitTransac;
import bootcamp.com.transactionms.utils.ConstantsPayMethod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class TransactionServiceTest {

  @Autowired
  private TransactionService transactionService;
  @MockBean
  private ITransactionRepository transactionRepository;
  @MockBean
  private FilterTransactionDebit filterTransactionDebit;
  @MockBean
  private FilterTransactionCredit filterTransactionCredit;
  @MockBean
  private FilterTransaction filterTransaction;
  @MockBean
  private WebClientProductHelper webClientProductHelper;

  private static final TransactionDto transactionDto = new TransactionDto();
  private static final Transaction transaction = new Transaction();
  private static final List<Transaction> transactionDtoList = new ArrayList<>();
  private static final String id = "61db9bc2b09be072956ae684";
  private static final String productId = "61db64d731dec743727907f3";
  private static final String fromProduct = "61db64ee31dec743727907f4";
  private static final String paymentMethod = "DIRECT";
  private static final String transactionType = "DEPOSIT";
  private static final double transactionAmount = 250;
  private static final double commission = 0;
  private static final String createdAt = "2022-01-14";
  private static final String createdBy = "pedro";
  private static final LocalDate updateAt = LocalDate.now();
  private static final String updateBy = "pedro";
  private static final String status = "COMPLETE";

  private static final ProductDto productDto = new ProductDto();
  private static final String idProduct = "61db64d731dec743727907f3";
  private static final String accountType = "SAVING";
  private static final String accountNumber = "d85c241a-2eb7-40da-938c-097f30d3756f";
  private static final String currency = "PEN";
  private static final double amount = 6300;
  private static final double maintenanceCommission = 0;
  private static final LocalDateTime maintenanceCommissionDay = null;
  private static final int maxTransactNumber = 10;
  private static final LocalDate transactNumberDay = null;
  private static final double creditLimit = 0;
  private static final String customer = "61db5ffd7610bd27a53b2b8b";
  private static final String statusProduct = "ACTIVE";
  private static final LocalDate createdAtProduct = LocalDate.now();
  private static final String createdByProduct = "pedro";
  private static final LocalDate updateAtProduct = LocalDate.now();
  private static final String updateByProduct = "pedro";
  private static final double minimumAverageAmount = 0;
  private static final double averageDailyBalance = 0;
  private static final LocalDate averageDailyBalanceDay = LocalDate.now();

  @BeforeAll
  static void setUp() {
    transactionDto.setId(id);
    transactionDto.setProductId(productId);
    transactionDto.setFromProduct(fromProduct);
    transactionDto.setPaymentMethod(paymentMethod);
    transactionDto.setTransactionType(transactionType);
    transactionDto.setTransactionAmount(transactionAmount);
    transactionDto.setCommission(commission);
    transactionDto.setCreatedAt(createdAt);
    transactionDto.setCreatedBy(createdBy);
    transactionDto.setUpdateAt(updateAt);
    transactionDto.setUpdateBy(updateBy);
    transactionDto.setStatus(status);
    BeanUtils.copyProperties(transactionDto, transaction);
    transactionDtoList.add(transaction);

    productDto.setId(idProduct);
    productDto.setAccountType(accountType);
    productDto.setAccountNumber(accountNumber);
    productDto.setCurrency(currency);
    productDto.setAmount(amount);
    productDto.setMaintenanceCommission(maintenanceCommission);
    productDto.setMaintenanceCommissionDay(maintenanceCommissionDay);
    productDto.setMaxTransactNumber(maxTransactNumber);
    productDto.setTransactNumberDay(transactNumberDay);
    productDto.setCreditLimit(creditLimit);
    productDto.setCustomer(customer);
    productDto.setStatus(statusProduct);
    productDto.setCreatedAt(createdAtProduct);
    productDto.setCreatedBy(createdByProduct);
    productDto.setUpdateAt(updateAtProduct);
    productDto.setUpdateBy(updateByProduct);
    productDto.setMinimumAverageAmount(minimumAverageAmount);
    productDto.setAverageDailyBalance(averageDailyBalance);
    productDto.setAverageDailyBalanceDay(averageDailyBalanceDay);
  }

  @Test
  void findAllTransaction() {
    when(transactionRepository.findAll()).thenReturn(Flux.just(transaction));
    Assertions.assertNotNull(transactionService.findAllTransaction());
  }

  @Test
  void findByIdTransaction() {
    when(transactionRepository.findById(id)).thenReturn(Mono.just(transaction));
    Assertions.assertNotNull(transactionService.findByIdTransaction(id));
  }

  @Test
  void findTransactionByProduct() {
    when(transactionRepository.findByProductId(productId)).thenReturn(Flux.just(transaction));
    Assertions.assertNotNull(transactionService.findTransactionByProduct(productId));
  }

  @Test
  void findCommissionByProduct() {
    when(transactionRepository.findByProductIdAndCreatedAtBetween(productId, "2022-01-13", "2022-01-16")).thenReturn(Flux.just(transaction));
    Assertions.assertNotNull(transactionService.findCommissionByProduct(productId, "2022-01-13", "2022-01-16"));
  }

  @Test
  void createTransactionDebit() {
    when(filterTransaction.filterTransactionCreate(transactionDto)).thenReturn(Mono.just(transactionDto));
    when(transactionRepository.findByProductId(productId)).thenReturn(Flux.just(transaction));
    when(filterTransactionDebit.filterDebit(transactionDto,productId,Flux.just(transaction))).thenReturn(Mono.just(transactionDto));
    when(transactionRepository.save(transaction)).thenReturn(Mono.just(transaction));
    Assertions.assertNotNull(transactionService.createTransactionDebit(transactionDto));
  }

  @Test
  void createTransferDebit() {
    transactionDto.setPaymentMethod(ConstantsPayMethod.TRANSFER.name());
    transactionDto.setTransactionType(ConstantsDebitTransac.TRANSFER.name());
    when(filterTransaction.filterTransactionCreate(transactionDto)).thenReturn(Mono.just(transactionDto));
    when(transactionRepository.findByProductId(productId)).thenReturn(Flux.just(transaction));
    when(filterTransactionDebit.filterDebit(transactionDto,productId,Flux.just(transaction))).thenReturn(Mono.just(transactionDto));
    when(filterTransactionDebit.isSave(transactionDto, productId, transactionDtoList)).thenReturn(Mono.just(transactionDto));
    when(transactionRepository.save(transaction)).thenReturn(Mono.just(transaction));
    Assertions.assertNotNull(transactionService.createTransferDebit(transactionDto));
  }

  @Test
  void updateTransactionDebit() {
    transactionDto.setPaymentMethod(ConstantsPayMethod.DIRECT.name());
    transactionDto.setTransactionType(ConstantsDebitTransac.DEPOSIT.name());
    BeanUtils.copyProperties(transactionDto,transaction);
    when(transactionRepository.findById(id)).thenReturn(Mono.just(transaction));
    when(transactionRepository.findByProductId(productId)).thenReturn(Flux.just(transaction));
    when(filterTransactionDebit.filterDebit(transactionDto,productId,Flux.just(transaction))).thenReturn(Mono.just(transactionDto));
    when(filterTransaction.filterTransactionUpdate(Mono.just(transactionDto))).thenReturn(Mono.just(transactionDto));
    when(transactionRepository.save(transaction)).thenReturn(Mono.just(transaction));
    Assertions.assertNotNull(transactionService.updateTransactionDebit(transactionDto,id));
  }

  @Test
  void removeTransactionDebit() {
    when(transactionRepository.findById(id)).thenReturn(Mono.just(transaction));
    when(filterTransactionDebit.filterRemoveProduct(transaction)).thenReturn(Mono.just(Boolean.TRUE));
    when(filterTransaction.filterTransactionDelete(transaction)).thenReturn(Mono.just(transaction));
    when(transactionRepository.save(transaction)).thenReturn(Mono.just(transaction));
    Assertions.assertNotNull(transactionService.removeTransactionDebit(id));
  }

  /*@Test
  void createTransactionCredit() {
    productDto.setAccountType(ConstantsCredit.CREDIT.name());
    transaction.setTransactionType(ConstantsCreditTransac.CREDIT_PAYMENT.name());
    when(filterTransaction.filterTransactionCreate(transactionDto)).thenReturn(Mono.just(transactionDto));
    when(transactionRepository.findByProductId(productId)).thenReturn(Flux.just(transaction));
    when(filterTransactionCredit.filterCredit(transactionDto,Flux.just(transaction))).thenReturn(Mono.just(productDto));
    when(filterTransactionCredit.isSave(transactionDto)).thenReturn(Mono.just(productDto));
    when(webClientProductHelper.findProduct(productId)).thenReturn(Mono.just(productDto));
    when(webClientProductHelper.updateProduct(productId,productDto)).thenReturn(Mono.just(productDto));
    when(transactionRepository.insert(transaction)).thenReturn(Mono.just(transaction));
    transactionDto.setPaymentMethod(ConstantsPayMethod.DIRECT.name());
    transactionDto.setTransactionType(ConstantsCreditTransac.CREDIT_PAYMENT.name());
    Assertions.assertNotNull(transactionService.createTransactionCredit(transactionDto));
  }*/

  @Test
  void updateTransactionCredit() {
    when(transactionRepository.findById(id)).thenReturn(Mono.just(transaction));
    when(filterTransaction.filterTransactionUpdate(Mono.just(transactionDto))).thenReturn(Mono.just(transactionDto));
    when(transactionRepository.save(transaction)).thenReturn(Mono.just(transaction));
    Assertions.assertNotNull(transactionService.updateTransactionCredit(transactionDto,id));
  }

  @Test
  void removeTransactionCredit() {
    when(transactionRepository.findById(id)).thenReturn(Mono.just(transaction));
    when(filterTransactionCredit.filterRemoveProduct(transaction)).thenReturn(Mono.just(Boolean.TRUE));
    when(filterTransaction.filterTransactionDelete(transaction)).thenReturn(Mono.just(transaction));
    when(transactionRepository.save(transaction)).thenReturn(Mono.just(transaction));
    Assertions.assertNotNull(transactionService.removeTransactionCredit(id));
  }
}