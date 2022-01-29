package bootcamp.com.transactionms.business.impl;

import static org.mockito.Mockito.when;
import bootcamp.com.transactionms.business.helper.FilterTransaction;
import bootcamp.com.transactionms.business.helper.FilterTransactionCredit;
import bootcamp.com.transactionms.business.helper.FilterTransactionDebit;
import bootcamp.com.transactionms.business.helper.WebClientProductHelper;
import bootcamp.com.transactionms.model.dto.ProductDto;
import bootcamp.com.transactionms.model.Transaction;
import bootcamp.com.transactionms.model.dto.TransactionDto;
import bootcamp.com.transactionms.repository.ITransactionRepository;
import bootcamp.com.transactionms.utils.*;
import com.google.gson.Gson;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.io.IOException;
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

  public static MockWebServer mockBackEnd;
  private static final TransactionDto transactionDto = new TransactionDto();
  private static final TransactionDto transactionDtoCredit = new TransactionDto();
  private static final Transaction transaction = new Transaction();
  private static final Transaction transactionCredit = new Transaction();
  private static final Transaction transactionRemove = new Transaction();
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
  private static final ProductDto productDtoCredit = new ProductDto();
  private static final String idProduct = "61db64d731dec743727907f3";
  private static final String accountType = "SAVING";
  private static final String accountNumber = "d558f2fb-dc37-4b32-ba9f-88b31d8efe10";
  private static final String subAccountNumber = "d558f2fb-dc37-4b32-ba9f-88b31d8efe10";
  private static final int level = 1;
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
  private static final LocalDate expiredDate = LocalDate.parse("2023-01-19");
  private static final String updateByProduct = "pedro";
  private static final double minimumAverageAmount = 0;
  private static final double averageDailyBalance = 0;
  private static final LocalDate averageDailyBalanceDay = LocalDate.now();

  @BeforeAll
  static void setUp(@Value("${server.port}") int port) throws IOException {
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
    BeanUtils.copyProperties(transactionDto, transactionRemove);
    BeanUtils.copyProperties(transactionDto, transactionDtoCredit);
    BeanUtils.copyProperties(transactionDtoCredit, transactionCredit);
    transactionDtoCredit.setTransactionType(ConstantsCreditTransac.CHARGE.name());

    transactionRemove.setStatus(ConstantsTransacStatus.REMOVE.name());
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
    productDto.setSubAccountNumber(subAccountNumber);
    productDto.setLevel(level);
    productDto.setExpiredDate(expiredDate);
    BeanUtils.copyProperties(productDto,productDtoCredit);
    productDtoCredit.setAccountType("CREDIT");
    productDtoCredit.setCreditLimit(25000);

    productDto.getAccountType();
    productDto.getLevel();
    productDto.getSubAccountNumber();
    productDto.getExpiredDate();
    productDto.getAccountNumber();
    productDto.getCurrency();
    productDto.getAmount();
    productDto.getMaintenanceCommission();
    productDto.getMaintenanceCommissionDay();
    productDto.getMaxTransactNumber();
    productDto.getTransactNumberDay();
    productDto.getCreditLimit();
    productDto.getCustomer();
    productDto.getStatus();
    productDto.getCreatedAt();
    productDto.getCreatedBy();
    productDto.getUpdateAt();
    productDto.getUpdateBy();
    productDto.getMinimumAverageAmount();
    productDto.getAverageDailyBalance();
    productDto.getAverageDailyBalanceDay();
    productDto.getId();
    mockBackEnd = new MockWebServer();
    mockBackEnd.start(port);
  }

  @AfterAll
  static void tearDown() throws IOException {
    mockBackEnd.shutdown();
  }

  @BeforeEach
  void setUp() {
    Gson gson = new Gson();
    mockBackEnd.url("http://localhost:9090/product");
    mockBackEnd.enqueue(new MockResponse()
      .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
      .setBody(gson.toJson(transactionDto))
      .setResponseCode(HttpStatus.OK.value()));
  }

  @Test
  void findAllTransaction() {
    when(transactionRepository.findAll()).thenReturn(Flux.just(transaction));
    Flux<TransactionDto> transactionDtoFlux = transactionService.findAllTransaction();
    StepVerifier
      .create(transactionDtoFlux)
      .consumeNextWith(newTransaction -> {
        Assertions.assertEquals(status, newTransaction.getStatus());
      })
      .verifyComplete();
  }

  @Test
  void findByIdTransaction() {
    when(transactionRepository.findById(id)).thenReturn(Mono.just(transaction));
    Mono<TransactionDto> transactionDtoMono = transactionService.findByIdTransaction(id);
    StepVerifier
      .create(transactionDtoMono)
      .consumeNextWith(newTransaction -> {
        Assertions.assertEquals(status, newTransaction.getStatus());
      })
      .verifyComplete();
  }

  @Test
  void findTransactionByProduct() {
    when(transactionRepository.findByProductId(productId)).thenReturn(Flux.just(transaction));
    Flux<TransactionDto> transactionDtoFlux = transactionService.findTransactionByProduct(productId);
    StepVerifier
      .create(transactionDtoFlux)
      .consumeNextWith(newTransaction -> {
        Assertions.assertEquals(status, newTransaction.getStatus());
      })
      .verifyComplete();
  }

  @Test
  void findTransactionByProductAndLimit() {
    when(transactionRepository.findByProductIdOrderByTransactionAmountDesc(productId)).thenReturn(Flux.just(transaction));
    Flux<TransactionDto> transactionDtoFlux = transactionService.findTransactionByProductAndLimit(productId);
    StepVerifier
      .create(transactionDtoFlux)
      .consumeNextWith(newTransaction -> {
        Assertions.assertEquals(status, newTransaction.getStatus());
      })
      .verifyComplete();
  }

  @Test
  void findCommissionByProduct() {
    when(transactionRepository.findByProductIdAndCreatedAtBetween(productId, "2022-01-13", "2022-01-16"))
      .thenReturn(Flux.just(transaction));
    Flux<TransactionDto> transactionDtoFlux = transactionService.findCommissionByProduct(productId, "2022-01-13", "2022-01-16");
    StepVerifier
      .create(transactionDtoFlux)
      .consumeNextWith(newTransaction -> {
        Assertions.assertEquals(status, newTransaction.getStatus());
      })
      .verifyComplete();
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
    when(transactionRepository.findByProductId(fromProduct)).thenReturn(Flux.just(transaction));
    when(filterTransactionDebit.filterDebit(transactionDto,productId,Flux.just(transaction))).thenReturn(Mono.just(transactionDto));
    when(filterTransactionDebit.filterDebit(transactionDto,fromProduct,Flux.just(transaction))).thenReturn(Mono.just(transactionDto));
    when(filterTransactionDebit.isSave(transactionDto, productId, transactionDtoList)).thenReturn(Mono.just(transactionDto));
    when(filterTransactionDebit.isSave(transactionDto, fromProduct, transactionDtoList)).thenReturn(Mono.just(transactionDto));
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
    when(filterTransactionDebit.updateDebits(transactionDto,transactionDto)).thenReturn(transactionDto);
    when(filterTransactionDebit.filterDebit(transactionDto,productId,Flux.just(transaction))).thenReturn(Mono.just(transactionDto));
    when(filterTransaction.filterTransactionUpdate(Mono.just(transactionDto))).thenReturn(Mono.just(transactionDto));
    when(transactionRepository.save(transaction)).thenReturn(Mono.just(transaction));
    Assertions.assertNotNull(transactionService.updateTransactionDebit(transactionDto,id));
  }

  @Test
  void removeTransactionDebit() {
    when(transactionRepository.findById(id)).thenReturn(Mono.just(transaction));
    when(filterTransactionDebit.filterRemoveProduct(transactionRemove)).thenReturn(Mono.just(Boolean.TRUE));
    when(filterTransaction.filterTransactionDelete(transaction)).thenReturn(Mono.just(transactionRemove));
    when(transactionRepository.save(transactionRemove)).thenReturn(Mono.just(transactionRemove));
    //Assertions.assertNotNull(transactionService.removeTransactionDebit(id));
    Mono<TransactionDto> transactionDtoMono = transactionService.removeTransactionDebit(id);
    StepVerifier
      .create(transactionDtoMono)
      .expectSubscription()
      .expectComplete();
  }

 /*@Test
  void createTransactionCredit() {
    when(transactionRepository.findByProductId(productId)).thenReturn(Flux.just(transactionCredit));
    when(filterTransactionCredit.filterCredit(transactionDtoCredit,Flux.just(transactionCredit))).thenReturn(Mono.just(transactionDtoCredit));
    when(filterTransaction.filterTransactionCreate(transactionDtoCredit)).thenReturn(Mono.just(transactionDtoCredit));
    when(transactionRepository.save(transaction)).thenReturn(Mono.just(transactionCredit));
    Mono<TransactionDto> transactionDtoMono = transactionService.createTransactionCredit(transactionDtoCredit);
    StepVerifier
      .create(transactionDtoMono)
      .expectNext(transactionDtoCredit)
      .
  }*/

  @Test
  void updateTransactionCredit() {
    when(transactionRepository.findById(id)).thenReturn(Mono.just(transaction));
    when(transactionRepository.findByProductId(productId)).thenReturn(Flux.just(transaction));
    when(filterTransaction.filterTransactionUpdate(Mono.just(transactionDto))).thenReturn(Mono.just(transactionDto));
    when(filterTransactionCredit.updateCredits(transactionDto,transactionDto)).thenReturn(transactionDto);
    when(filterTransactionCredit.filterCredit(transactionDto,Flux.fromIterable(transactionDtoList))).thenReturn(Mono.just(transactionDto));
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