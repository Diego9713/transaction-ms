package bootcamp.com.transactionms.expose;

import bootcamp.com.transactionms.business.ITransactionCoinPurseService;
import bootcamp.com.transactionms.business.ITransactionService;
import bootcamp.com.transactionms.model.dto.TransactionDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/v1/transactions")
public class TransactionController {
  @Autowired
  @Qualifier("TransactionService")
  private ITransactionService transactionService;

  @Autowired
  @Qualifier("TransactionCoinPurseService")
  private ITransactionCoinPurseService transactionCoinPurseService;

  /**
   * Method to find all transactions.
   *
   * @return a list of transaction.
   */
  @GetMapping("")
  public Flux<TransactionDto> findAllTransaction() {
    return transactionService.findAllTransaction();
  }

  /**
   * Method to find the bank movements that a customer has.
   *
   * @param productId -> identifier of la account.
   * @return a list transaction.
   */
  @GetMapping("/product/{productId}")
  public Flux<TransactionDto> findTransactionByProduct(@PathVariable("productId") String productId) {
    return transactionService.findTransactionByProduct(productId);
  }

  /**
   * Method to find the  ten movements that a product.
   *
   * @param productId -> identifier of la account.
   * @return a list transaction.
   */
  @GetMapping("/product/{productId}/limit")
  public Flux<TransactionDto> findTransactionByProductAndLimit(@PathVariable("productId") String productId) {
    return transactionService.findTransactionByProduct(productId);
  }

  /**
   * Method to find the bank movements that a customer has.
   *
   * @param productId -> identifier of la account.
   * @return a list transaction.
   */
  @GetMapping("/commission/{productId}")
  public Flux<TransactionDto> findCommissionByProduct(@PathVariable("productId") String productId,
                                                      @RequestParam("from") String from,
                                                      @RequestParam("until") String until) {
    return transactionService.findCommissionByProduct(productId, from, until);
  }

  /**
   * Method to search for a transaction by id.
   *
   * @param id -> identifier of transaction.
   * @return object of transaction.
   */
  @GetMapping("/{id}")
  public Mono<ResponseEntity<TransactionDto>> findOneTransaction(@PathVariable String id) {
    return transactionService.findByIdTransaction(id)
      .flatMap(p -> Mono.just(ResponseEntity.ok().body(p)))
      .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));

  }

  /**
   * Method to save a transaction type Debits.
   *
   * @param transaction -> attribute object type transaction.
   * @return the transaction saved.
   */
  @CircuitBreaker(name = "postTransactionDebitCB", fallbackMethod = "fallBackPostTransactionDebit")
  @PostMapping("/debits")
  public Mono<ResponseEntity<TransactionDto>> saveTransactionDebit(@RequestBody TransactionDto transaction) {
    return transactionService.createTransactionDebit(transaction)
      .flatMap(p -> Mono.just(ResponseEntity.ok().body(p)))
      .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
  }

  /**
   * Method for making transfers between accounts.
   *
   * @param transaction -> attribute object type transaction.
   * @return the transaction saved.
   */
  @CircuitBreaker(name = "postTransferDebitCB", fallbackMethod = "fallBackPostTransferDebit")
  @PostMapping("/debits/transfer")
  public Mono<ResponseEntity<TransactionDto>> saveTransferDebit(@RequestBody TransactionDto transaction) {
    return transactionService.createTransferDebit(transaction)
      .flatMap(p -> Mono.just(ResponseEntity.ok().body(p)))
      .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
  }

  /**
   * Method to update a transaction type Debits.
   *
   * @param transaction -> attribute object type transaction.
   * @param id          -> identifier of transaction.
   * @return the transaction update.
   */
  @CircuitBreaker(name = "putTransactionDebitCB", fallbackMethod = "fallBackPutTransactionDebit")
  @PutMapping("/debits/{id}")
  public Mono<ResponseEntity<TransactionDto>> updateTransactionDebit(@PathVariable String id,
                                                                     @RequestBody TransactionDto transaction) {
    return transactionService.updateTransactionDebit(transaction, id)
      .flatMap(p -> Mono.just(ResponseEntity.ok().body(p)))
      .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }

  /**
   * Method to remove a transaction type Debits.
   *
   * @param id -> identifier of transaction.
   * @return the transaction remove.
   */
  @CircuitBreaker(name = "deleteTransactionDebitCB", fallbackMethod = "fallBackDeleteTransactionDebit")
  @DeleteMapping("/debits/{id}")
  public Mono<ResponseEntity<TransactionDto>> removeTransactionDebit(@PathVariable String id) {
    return transactionService.removeTransactionDebit(id)
      .flatMap(p -> Mono.just(ResponseEntity.ok().body(p)))
      .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }

  /**
   * Method to save a transaction type Credits.
   *
   * @param transaction -> attribute object type transaction.
   * @return the transaction saved.
   */
  @CircuitBreaker(name = "postTransactionCreditCB", fallbackMethod = "fallBackPostTransactionCredit")
  @PostMapping("/credits")
  public Mono<ResponseEntity<TransactionDto>> saveTransactionCredit(@RequestBody TransactionDto transaction) {
    return transactionService.createTransactionCredit(transaction)
      .flatMap(p -> Mono.just(ResponseEntity.ok().body(p)))
      .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
  }

  /**
   * Method to update a transaction type Credits.
   *
   * @param transaction -> attribute object type transaction.
   * @param id          -> identifier of transaction.
   * @return the transaction update.
   */
  @CircuitBreaker(name = "putTransactionCreditCB", fallbackMethod = "fallBackPutTransactionCredit")
  @PutMapping("/credits/{id}")
  public Mono<ResponseEntity<TransactionDto>> updateTransactionCredit(@PathVariable String id,
                                                                      @RequestBody TransactionDto transaction) {
    return transactionService.updateTransactionCredit(transaction, id)
      .flatMap(p -> Mono.just(ResponseEntity.ok().body(p)))
      .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }

  /**
   * Method to remove a transaction type Credits.
   *
   * @param id -> identifier of transaction.
   * @return the transaction remove.
   */
  @CircuitBreaker(name = "deleteTransactionCreditCB", fallbackMethod = "fallBackDeleteTransactionCredit")
  @DeleteMapping("/credits/{id}")
  public Mono<ResponseEntity<TransactionDto>> removeTransactionCredit(@PathVariable("id") String id) {
    return transactionService.removeTransactionCredit(id)
      .flatMap(p -> Mono.just(ResponseEntity.ok().body(p)))
      .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }

  /**
   * Method to Create Transaction for coin purse.
   *
   * @param transaction -> object to create.
   * @return object created transaction.
   */
  @CircuitBreaker(name = "postCoinPurseCB", fallbackMethod = "fallBackPostCoinPurse")
  @PostMapping("/coinpurse")
  public Mono<ResponseEntity<TransactionDto>> saveTransactionCoinPurse(@RequestBody TransactionDto transaction) {
    return transactionCoinPurseService.createTransactionCoinPurse(transaction)
      .flatMap(p -> Mono.just(ResponseEntity.ok().body(p)))
      .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
  }

  /**
   * Method to update transaction for coin purse.
   *
   * @param id          -> identify unique of transaction.
   * @param transaction -> object to create.
   * @return object updated.
   */
  @CircuitBreaker(name = "putCoinPurseCB", fallbackMethod = "fallBackPutCoinPurse")
  @PutMapping("/coinpurse/{id}")
  public Mono<ResponseEntity<TransactionDto>> updateTransactionCoinPurse(@PathVariable("id") String id,
                                                                         @RequestBody TransactionDto transaction) {
    return transactionCoinPurseService.updateTransactionCoinPurse(transaction, id)
      .flatMap(p -> Mono.just(ResponseEntity.ok().body(p)))
      .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
  }

  /**
   * Method to remove transaction of coin purse.
   *
   * @param id -> identify unique of transaction.
   * @return object change status.
   */
  @CircuitBreaker(name = "deleteCoinPurseCB", fallbackMethod = "fallBackDeleteCoinPurse")
  @DeleteMapping("/coinpurse/{id}")
  public Mono<ResponseEntity<TransactionDto>> deleteTransactionCoinPurse(@PathVariable("id") String id) {
    return transactionCoinPurseService.removeTransactionCoinPurse(id)
      .flatMap(p -> Mono.just(ResponseEntity.ok().body(p)))
      .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
  }

  /**
   * Method CircuitBreaker to save transaction debit.
   *
   * @param ex -> this is exception error.
   * @return exception error.
   */
  public Mono<ResponseEntity<String>> fallBackPostTransactionDebit(@RequestBody TransactionDto transaction,
                                                                   RuntimeException ex) {
    return Mono.just(ResponseEntity.ok().body("Search for the "
      + transaction.getProductId() + " in the service not available."));
  }

  /**
   * Method CircuitBreaker to save Transfer debit.
   *
   * @param ex -> this is exception error.
   * @return exception error.
   */
  public Mono<ResponseEntity<String>> fallBackPostTransferDebit(@RequestBody TransactionDto transaction,
                                                                RuntimeException ex) {
    return Mono.just(ResponseEntity.ok().body("Transfer with "
      + transaction.getProductId() + " not available microservice."));
  }

  /**
   * Method CircuitBreaker to update Transaction debit.
   *
   * @param ex -> this is exception error.
   * @return exception error.
   */
  public Mono<ResponseEntity<String>> fallBackPutTransactionDebit(@PathVariable String id,
                                                                  @RequestBody TransactionDto transaction,
                                                                  RuntimeException ex) {
    return Mono.just(ResponseEntity.ok().body("update to "
      + id + " and transaction " + transaction.getId() + " not available."));
  }

  /**
   * Method CircuitBreaker to delete Transaction debit.
   *
   * @param ex -> this is exception error.
   * @return exception error.
   */
  public Mono<ResponseEntity<String>> fallBackDeleteTransactionDebit(@PathVariable String id, RuntimeException ex) {
    return Mono.just(ResponseEntity.ok().body("delete to " + id + " not available"));
  }

  /**
   * Method CircuitBreaker to post Transaction credit.
   *
   * @param ex -> this is exception error.
   * @return exception error.
   */
  public Mono<ResponseEntity<String>> fallBackPostTransactionCredit(@RequestBody TransactionDto transaction,
                                                                    RuntimeException ex) {
    return Mono.just(ResponseEntity.ok().body("post to credit with "
      + transaction.getProductId() + " not available method"));
  }

  /**
   * Method CircuitBreaker to update Transaction credit.
   *
   * @param ex -> this is exception error.
   * @return exception error.
   */
  public Mono<ResponseEntity<String>> fallBackPutTransactionCredit(@PathVariable String id,
                                                                   @RequestBody TransactionDto transaction,
                                                                   RuntimeException ex) {
    return Mono.just(ResponseEntity.ok().body("update to credit with "
      + id + "and transaction " + transaction.getProductId()
      + " not available method."));
  }

  /**
   * Method CircuitBreaker to delete Transaction credit.
   *
   * @param ex -> this is exception error.
   * @return exception error.
   */
  public Mono<ResponseEntity<String>> fallBackDeleteTransactionCredit(@PathVariable String id, RuntimeException ex) {
    return Mono.just(ResponseEntity.ok().body("delete to credit with " + id + " not available"));
  }

  /**
   * Method CircuitBreaker to Post Transaction coin purse.
   *
   * @param transaction -> Object transaction.
   * @param ex -> this is exception error.
   * @return exception error.
   */
  public Mono<ResponseEntity<String>> fallBackPostCoinPurse(@RequestBody TransactionDto transaction,
                                                            RuntimeException ex) {
    return Mono.just(ResponseEntity.ok().body("Save Coin Purse not available"));
  }

  /**
   * Method CircuitBreaker to Update Transaction coin purse.
   *
   * @param transaction -> Object transaction.
   * @param ex -> this is exception error.
   * @return exception error.
   */
  public Mono<ResponseEntity<String>> fallBackPutCoinPurse(@PathVariable("id") String id,
                                                           @RequestBody TransactionDto transaction,
                                                           RuntimeException ex) {
    return Mono.just(ResponseEntity.ok().body("Update " + id + " Coin Purse not available"));
  }

  /**
   * Method CircuitBreaker to Delete Transaction coin purse.
   *
   * @param id -> Identify unique transaction.
   * @param ex -> this is exception error.
   * @return exception error.
   */
  public Mono<ResponseEntity<String>> fallBackDeleteCoinPurse(@PathVariable("id") String id, RuntimeException ex) {
    return Mono.just(ResponseEntity.ok().body("Delete Coin Purse not available"));
  }

}
