package bootcamp.com.transactionms.expose;

import bootcamp.com.transactionms.business.ITransactionService;
import bootcamp.com.transactionms.model.TransactionDto;
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
  @DeleteMapping("/credits/{id}")
  public Mono<ResponseEntity<TransactionDto>> removeTransactionCredit(@PathVariable("id") String id) {
    return transactionService.removeTransactionCredit(id)
      .flatMap(p -> Mono.just(ResponseEntity.ok().body(p)))
      .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }
}
