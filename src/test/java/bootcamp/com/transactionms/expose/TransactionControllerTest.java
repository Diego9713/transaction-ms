package bootcamp.com.transactionms.expose;

import bootcamp.com.transactionms.business.impl.TransactionService;
import bootcamp.com.transactionms.model.Transaction;
import bootcamp.com.transactionms.model.dto.TransactionDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.mockito.Mockito.when;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "20000")
class TransactionControllerTest {

  @Autowired
  private TransactionController transactionController;

  @Autowired
  private WebTestClient webTestClient;

  @MockBean
  private TransactionService transactionService;

  private static final TransactionDto transactionDto = new TransactionDto();
  private static final Transaction transaction = new Transaction();
  private static final String id = "61db9bc2b09be072956ae684";
  private static final String productId = "61db64d731dec743727907f3";
  private static final String fromProduct = "61db64ee31dec743727907f4";
  private static final String paymentMethod = "TRANSFER";
  private static final String transactionType = "TRANSFER";
  private static final double transactionAmount = 250;
  private static final double commission = 0;
  private static final String createdAt = "2022-01-14";
  private static final String createdBy = "pedro";
  private static final LocalDate updateAt = LocalDate.now();
  private static final String updateBy = "pedro";
  private static final String status = "COMPLETE";

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
  }

  @Test
  @DisplayName("GET -> /api/v1/transactions")
  void findAllTransaction() {
    when(transactionService.findAllTransaction()).thenReturn(Flux.just(transactionDto));

    WebTestClient.ResponseSpec responseSpec = webTestClient.get()
      .uri("/api/v1/transactions")
      .accept(MediaType.APPLICATION_JSON)
      .exchange();

    responseSpec.expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON);
  }

  @Test
  @DisplayName("GET -> /api/v1/transactions/product/{productId}")
  void findTransactionByProduct() {
    when(transactionService.findTransactionByProduct(productId)).thenReturn(Flux.just(transactionDto));

    WebTestClient.ResponseSpec responseSpec = webTestClient.get()
      .uri("/api/v1/transactions/product/" + productId)
      .accept(MediaType.APPLICATION_JSON)
      .exchange();

    responseSpec.expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON);
  }

  @Test
  @DisplayName("GET -> /api/v1/transactions/product/{productId}/limit")
  void findTransactionByProductAndLimit() {
    when(transactionService.findTransactionByProductAndLimit(productId)).thenReturn(Flux.just(transactionDto));

    WebTestClient.ResponseSpec responseSpec = webTestClient.get()
      .uri("/api/v1/transactions/product/" + productId + "/limit")
      .accept(MediaType.APPLICATION_JSON)
      .exchange();

    responseSpec.expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON);
  }

  @Test
  @DisplayName("GET -> /api/v1/transactions/commission/{productId}")
  void findCommissionByProduct() {
    when(transactionService.findCommissionByProduct(productId, "2022-01-13", "2022-01-16"))
      .thenReturn(Flux.just(transactionDto));

    WebTestClient.ResponseSpec responseSpec = webTestClient.get()
      .uri("/api/v1/transactions/commission/" + productId + "?from=2022-01-13&until=2022-01-16")
      .accept(MediaType.APPLICATION_JSON)
      .exchange();

    responseSpec.expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON);
  }

  @Test
  @DisplayName("GET -> /api/v1/transactions/{id}")
  void findOneTransaction() {
    when(transactionService.findByIdTransaction(id)).thenReturn(Mono.just(transactionDto));

    WebTestClient.ResponseSpec responseSpec = webTestClient.get().uri("/api/v1/transactions/" + id)
      .accept(MediaType.APPLICATION_JSON)
      .exchange();

    responseSpec.expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON);
    responseSpec.expectBody()
      .jsonPath("$.id").isEqualTo(transactionDto.getId());
  }

  @Test
  @DisplayName("POST -> /api/v1/transactions/debits")
  void saveTransactionDebit() {
    when(transactionService.createTransactionDebit(transactionDto)).thenReturn(Mono.just(transactionDto));
    Assertions.assertNotNull(transactionController.saveTransactionDebit(transactionDto));
  }

  @Test
  @DisplayName("POST -> /api/v1/transactions/debits/transfer")
  void saveTransferDebit() {
    when(transactionService.createTransferDebit(transactionDto)).thenReturn(Mono.just(transactionDto));
    Assertions.assertNotNull(transactionController.saveTransferDebit(transactionDto));
  }

  @Test
  @DisplayName("PUT -> /api/v1/transactions/debits/{id}")
  void updateTransactionDebit() {
    when(transactionService.updateTransactionDebit(transactionDto, id)).thenReturn(Mono.just(transactionDto));
    Assertions.assertNotNull(transactionController.updateTransactionDebit(id, transactionDto));
  }

  @Test
  @DisplayName("DELETE -> /api/v1/transactions/debits/{id}")
  void removeTransactionDebit() {
    when(transactionService.removeTransactionDebit(id)).thenReturn(Mono.just(transactionDto));

    WebTestClient.ResponseSpec responseSpec = webTestClient.delete()
      .uri("/api/v1/transactions/debits/" + id)
      .accept(MediaType.APPLICATION_JSON)
      .exchange();

    responseSpec.expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON);
    responseSpec.expectBody()
      .jsonPath("$.id").isEqualTo(transactionDto.getId());
  }

  @Test
  @DisplayName("POST -> /api/v1/transactions/credits")
  void saveTransactionCredit() {
    when(transactionService.createTransactionCredit(transactionDto)).thenReturn(Mono.just(transactionDto));
    Assertions.assertNotNull(transactionController.saveTransactionCredit(transactionDto));
  }

  @Test
  @DisplayName("POST -> /api/v1/transactions/credits/{id}")
  void updateTransactionCredit() {
    when(transactionService.updateTransactionCredit(transactionDto, id)).thenReturn(Mono.just(transactionDto));
    Assertions.assertNotNull(transactionController.updateTransactionCredit(id, transactionDto));
  }

  @Test
  @DisplayName("DELETE -> /api/v1/transactions/credits/{id}")
  void removeTransactionCredit() {
    when(transactionService.removeTransactionCredit(id)).thenReturn(Mono.just(transactionDto));

    WebTestClient.ResponseSpec responseSpec = webTestClient.delete().uri("/api/v1/transactions/credits/" + id)
      .accept(MediaType.APPLICATION_JSON)
      .exchange();

    responseSpec.expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON);
    responseSpec.expectBody()
      .jsonPath("$.id").isEqualTo(transactionDto.getId());
  }

  @Test
  void fallBackPostTransactionDebit() {
    Assertions.assertNotNull(transactionController.fallBackPostTransactionDebit(transactionDto, new RuntimeException("")));
  }

  @Test
  void fallBackPostTransferDebit() {
    Assertions.assertNotNull(transactionController.fallBackPostTransferDebit(transactionDto, new RuntimeException("")));
  }

  @Test
  void fallBackPutTransactionDebit() {
    Assertions.assertNotNull(transactionController.fallBackPutTransactionDebit(id, transactionDto, new RuntimeException("")));
  }

  @Test
  void fallBackDeleteTransactionDebit() {
    Assertions.assertNotNull(transactionController.fallBackDeleteTransactionDebit(id, new RuntimeException("")));
  }

  @Test
  void fallBackPostTransactionCredit() {
    Assertions.assertNotNull(transactionController.fallBackPostTransactionCredit(transactionDto, new RuntimeException("")));
  }

  @Test
  void fallBackPutTransactionCredit() {
    Assertions.assertNotNull(transactionController.fallBackPutTransactionCredit(id, transactionDto, new RuntimeException("")));
  }

  @Test
  void fallBackDeleteTransactionCredit() {
    Assertions.assertNotNull(transactionController.fallBackDeleteTransactionCredit(id, new RuntimeException("")));
  }

}