package bootcamp.com.transactionms.business.helper;

import bootcamp.com.transactionms.model.ProductDto;
import bootcamp.com.transactionms.model.Transaction;
import bootcamp.com.transactionms.model.TransactionDto;
import bootcamp.com.transactionms.utils.ConstantsDebit;
import bootcamp.com.transactionms.utils.ConstantsDebitTransac;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilterTransactionDebitTest {
  @Autowired
  private FilterTransactionDebit filterTransactionDebit;
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
  private static final LocalDateTime maintenanceCommissionDay = LocalDateTime.now();
  private static final int maxTransactNumber = 10;
  private static final LocalDateTime transactNumberDay = LocalDateTime.now();
  private static final double creditLimit = 0;
  private static final String customer = "61db5ffd7610bd27a53b2b8b";
  private static final String statusProduct = "ACTIVE";
  private static final LocalDateTime createdAtProduct = LocalDateTime.now();
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
  void isSave() {
    Mockito.when(webClientProductHelper.findProduct(productId)).thenReturn(Mono.just(productDto));
    Assertions.assertNotNull(filterTransactionDebit.isSave(transactionDto,productId,transactionDtoList));
  }

  @Test
  void isTypeTransact() {
    Mockito.when(webClientProductHelper.updateProduct(productId,productDto)).thenReturn(Mono.just(productDto));
    transactionDto.setTransactionType(ConstantsDebitTransac.DEPOSIT.name());
    Assertions.assertNotNull(filterTransactionDebit.isTypeTransact(transactionDto,productDto,transactionDtoList));
    transactionDto.setTransactionType(ConstantsDebitTransac.WITHDRAWAL.name());
    productDto.setAccountType(ConstantsDebit.CURRENT.name());
    Assertions.assertNotNull(filterTransactionDebit.isTypeTransact(transactionDto,productDto,transactionDtoList));
    productDto.setAccountType(ConstantsDebit.FIXED_TERM.name());
    Assertions.assertNotNull(filterTransactionDebit.isTypeTransact(transactionDto,productDto,transactionDtoList));
    transactionDto.setTransactionType(ConstantsDebitTransac.TRANSFER.name());
    productDto.setAccountType(ConstantsDebit.FIXED_TERM.name());
    Assertions.assertNotNull(filterTransactionDebit.isTypeTransact(transactionDto,productDto,transactionDtoList));
  }

  @Test
  void filterProductTransfer() {
    Mockito.when(webClientProductHelper.updateProduct(productId,productDto)).thenReturn(Mono.just(productDto));
    Assertions.assertNotNull(filterTransactionDebit.filterProductTransfer(transactionDto,productDto,transactionDtoList));
    transactionDto.setFromProduct(productId);
    Assertions.assertNotNull(filterTransactionDebit.filterProductTransfer(transactionDto,productDto,transactionDtoList));
  }

  @Test
  void filterRemoveProduct() {
    Mockito.when(webClientProductHelper.findProduct(productId)).thenReturn(Mono.just(productDto));

    transaction.setTransactionType(ConstantsDebitTransac.DEPOSIT.name());
    Mockito.when(webClientProductHelper.updateProduct(productId,productDto)).thenReturn(Mono.just(productDto));
    Assertions.assertNotNull(filterTransactionDebit.filterRemoveProduct(transaction));

    transaction.setTransactionType(ConstantsDebitTransac.WITHDRAWAL.name());
    Mockito.when(webClientProductHelper.updateProduct(productId,productDto)).thenReturn(Mono.just(productDto));
    Assertions.assertNotNull(filterTransactionDebit.filterRemoveProduct(transaction));
  }

  @Test
  void filterDebit() {
    Mockito.when(webClientProductHelper.findProduct(productId)).thenReturn(Mono.just(productDto));
    Assertions.assertNotNull(filterTransactionDebit.filterDebit(transactionDto,productId, Flux.fromIterable(transactionDtoList)));
  }
}