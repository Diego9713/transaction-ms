package bootcamp.com.transactionms.business.impl;

import bootcamp.com.transactionms.business.ITransactionService;
import bootcamp.com.transactionms.business.helper.FilterTransaction;
import bootcamp.com.transactionms.business.helper.FilterTransactionCredit;
import bootcamp.com.transactionms.business.helper.FilterTransactionDebit;
import bootcamp.com.transactionms.model.ProductDto;
import bootcamp.com.transactionms.model.Transaction;
import bootcamp.com.transactionms.model.TransactionDto;
import bootcamp.com.transactionms.repository.ITransactionRepository;
import bootcamp.com.transactionms.utils.AppUtils;
import bootcamp.com.transactionms.utils.ConstantsTransacStatus;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service("TransactionService")
@Slf4j
public class TransactionService implements ITransactionService {

  @Autowired
  private ITransactionRepository transactionRepository;
  @Autowired
  private FilterTransactionDebit filterTransactionDebit;
  @Autowired
  private FilterTransactionCredit filterTransactionCredit;
  @Autowired
  private FilterTransaction filterTransaction;

  /**
   * Method to find all transactions.
   *
   * @return a list of transaction.
   */
  @Override
  @Transactional(readOnly = true)
  public Flux<TransactionDto> findAllTransaction() {
    log.info("FindAll >>>");
    return transactionRepository.findAll()
      .map(AppUtils::entityToTransactionDto);
  }

  /**
   * Method to search for a transaction by id.
   *
   * @param id -> identifier of transaction.
   * @return object of transaction.
   */
  @Override
  @Transactional(readOnly = true)
  public Mono<TransactionDto> findByIdTransaction(String id) {
    log.info("FindById >>>");
    return transactionRepository.findById(id)
      .map(AppUtils::entityToTransactionDto)
      .filter(transactionDto -> transactionDto.getStatus()
        .equalsIgnoreCase(ConstantsTransacStatus.COMPLETE.name()));
  }

  /**
   * Method to search for a transaction by product.
   *
   * @param productId -> identifier of product.
   * @return object of transaction.
   */
  @Override
  @Transactional(readOnly = true)
  public Flux<TransactionDto> findTransactionByProduct(String productId) {
    log.info("Find transactionByProduct >>>");
    return transactionRepository.findByProductId(productId)
      .map(AppUtils::entityToTransactionDto)
      .filter(transactionDto -> transactionDto.getStatus()
        .equalsIgnoreCase(ConstantsTransacStatus.COMPLETE.name()));
  }

  /**
   * Method to find the bank movements that a customer has.
   *
   * @param productId -> identifier of la account.
   * @return a list transaction.
   */
  @Override
  @Transactional(readOnly = true)
  public Flux<TransactionDto> findCommissionByProduct(String productId, String fromDate, String untilDate) {
    return transactionRepository.findByProductIdAndCreatedAtBetween(productId, fromDate, untilDate)
      .map(AppUtils::entityToTransactionDto);
  }

  /**
   * Method to save a transaction type Debits.
   *
   * @param transaction -> attribute object type transaction.
   * @return the transaction saved.
   */
  @Override
  @Transactional
  public Mono<TransactionDto> createTransactionDebit(TransactionDto transaction) {
    log.info("Save Transaction Debit >>>");
    Mono<TransactionDto> filterCreateTransaction = filterTransaction
        .filterTransactionCreate(transaction);
    Mono<TransactionDto> filterProduct = filterCreateTransaction
        .flatMap(transaction1 -> filterDebit(transaction1, transaction.getProductId()));
    return filterProduct.map(AppUtils::transactionDtoToEntity)
      .flatMap(transaction1 -> transaction1.getProductId() != null
      ? transactionRepository.save(transaction1).map(AppUtils::entityToTransactionDto)
      : Mono.empty());

  }

  /**
   * Method for making transfers between accounts.
   *
   * @param transaction -> attribute object type transaction.
   * @return the transaction saved.
   */
  @Override
  @Transactional
  public Mono<TransactionDto> createTransferDebit(TransactionDto transaction) {
    log.info("Save Transfer Debit >>>");
    Mono<TransactionDto> filterCreateTransaction = filterTransaction.filterTransactionCreate(transaction);
    Mono<TransactionDto> transfer = filterCreateTransaction.flatMap(t -> filterDebit(t, transaction.getProductId())
        .filter(afterTransaction -> afterTransaction.getProductId() != null)
        .flatMap(next -> filterDebit(t, transaction.getFromProduct())));
    return transfer.filter(t -> t.getProductId() != null)
      .map(AppUtils::transactionDtoToEntity)
      .flatMap(transactionRepository::save).map(AppUtils::entityToTransactionDto);
  }

  /**
   * Method to update a transaction type Debits.
   *
   * @param transaction -> attribute object type transaction.
   * @param id          -> identifier of transaction.
   * @return the transaction update.
   */
  @Override
  @Transactional
  public Mono<TransactionDto> updateTransactionDebit(TransactionDto transaction, String id) {
    log.info("Update Transaction Debit >>>");
    Mono<TransactionDto> findTransaction = transactionRepository.findById(id).map(AppUtils::entityToTransactionDto);
    Mono<TransactionDto> filterProduct = findTransaction.switchIfEmpty(Mono.empty())
        .flatMap(transaction1 -> {
          transaction.setId(id);
          if (transaction1.getTransactionAmount() > transaction.getTransactionAmount()) {
            transaction.setTransactionAmount(transaction1.getTransactionAmount() - transaction.getTransactionAmount());
          } else {
            transaction.setTransactionAmount(transaction.getTransactionAmount() - transaction1.getTransactionAmount());
          }
          return filterDebit(transaction, transaction.getProductId());
        });
    return filterProduct.flatMap(transaction1 -> transaction1.getId() != null
      ? filterTransaction.filterTransactionUpdate(findTransaction)
        .map(AppUtils::transactionDtoToEntity)
        .flatMap(transactionModel -> {
          transactionModel.setId(id);
          return transactionRepository.save(transactionModel);
        })
        .map(AppUtils::entityToTransactionDto)
      : Mono.empty());
  }

  /**
   * Method to remove a transaction type Debits.
   *
   * @param id -> identifier of transaction.
   * @return the transaction remove.
   */
  @Override
  @Transactional
  public Mono<TransactionDto> removeTransactionDebit(String id) {
    log.info("Remove Transaction Debit >>>");
    return transactionRepository.findById(id).switchIfEmpty(Mono.empty())
      .filter(transaction -> transaction.getStatus()
        .equalsIgnoreCase(ConstantsTransacStatus.COMPLETE.name()))
      .flatMap(transaction -> filterTransactionDebit.filterRemoveProduct(transaction)
        .flatMap(optionRemove -> Boolean.TRUE.equals(optionRemove)
          ? filterTransaction.filterTransactionDelete(transaction)
            .flatMap(transactionRepository::save)
            .map(AppUtils::entityToTransactionDto)
          : Mono.just(new TransactionDto())));
  }

  /**
   * Method to save a transaction type Credits.
   *
   * @param transaction -> attribute object type transaction.
   * @return the transaction saved.
   */
  @Override
  @Transactional
  public Mono<TransactionDto> createTransactionCredit(TransactionDto transaction) {
    log.info("Save Transaction Credit >>>");
    Mono<ProductDto> filterProduct = filterCredit(transaction);
    return filterProduct.flatMap(productDto -> productDto.getId() != null
      ? filterTransaction.filterTransactionCreate(transaction)
        .map(AppUtils::transactionDtoToEntity)
        .flatMap(transactionRepository::insert)
        .map(AppUtils::entityToTransactionDto)
      : Mono.just(new TransactionDto()));
  }

  /**
   * Method to update a transaction type Credits.
   *
   * @param transaction -> attribute object type transaction.
   * @param id          -> identifier of transaction.
   * @return the transaction update.
   */
  @Override
  @Transactional
  public Mono<TransactionDto> updateTransactionCredit(TransactionDto transaction, String id) {
    log.info("Update Transaction Credit >>>");
    Mono<TransactionDto> findTransaction = transactionRepository.findById(id).map(AppUtils::entityToTransactionDto);
    Mono<ProductDto> filterProduct = findTransaction.switchIfEmpty(Mono.empty())
        .flatMap(transaction1 -> filterCredit(transaction));
    return filterProduct.flatMap(productDto -> productDto.getId() != null
      ? filterTransaction.filterTransactionUpdate(findTransaction)
        .map(AppUtils::transactionDtoToEntity)
        .flatMap(transactionModel -> {
          transactionModel.setId(id);

          if (transactionModel.getTransactionAmount() > transaction.getTransactionAmount()) {
            transactionModel.setTransactionAmount(
                transactionModel.getTransactionAmount() - transaction.getTransactionAmount());
          } else {
            transactionModel.setTransactionAmount(
                transaction.getTransactionAmount() - transactionModel.getTransactionAmount());
          }
          return transactionRepository.save(transactionModel);
        })
        .map(AppUtils::entityToTransactionDto)
      : Mono.just(new TransactionDto()));
  }

  /**
   * Method to remove a transaction type Credits.
   *
   * @param id -> identifier of transaction.
   * @return the transaction remove.
   */
  @Override
  @Transactional
  public Mono<TransactionDto> removeTransactionCredit(String id) {
    log.info("Remove Transaction Credit >>>");
    return transactionRepository.findById(id)
      .switchIfEmpty(Mono.empty())
      .filter(transaction -> transaction.getStatus()
        .equalsIgnoreCase(ConstantsTransacStatus.COMPLETE.name()))
      .flatMap(transaction -> filterTransactionCredit.filterRemoveProduct(transaction)
        .flatMap(optionRemove -> Boolean.TRUE.equals(optionRemove)
          ? filterTransaction.filterTransactionDelete(transaction)
            .flatMap(transactionRepository::save)
            .map(AppUtils::entityToTransactionDto)
          : Mono.just(new TransactionDto())));
  }

  /**
   * Method to condition the saving of the transaction Debits
   *
   * @param transaction -> attribute object type transaction.
   * @return the condition for saving.
   */
  public Mono<TransactionDto> filterDebit(TransactionDto transaction, String id) {
    log.info(" Filter Transaction Debit >>>");
    Mono<List<Transaction>> transactionFlux = transactionRepository.findByProductId(id).collectList();
    return transactionFlux.flatMap(transactionList -> filterTransactionDebit.isSave(transaction, id, transactionList));
  }

  /**
   * Method to condition the saving of the transaction Credits
   *
   * @param transaction -> attribute object type transaction.
   * @return the condition for saving.
   */
  public Mono<ProductDto> filterCredit(TransactionDto transaction) {
    log.info(" Filter Transaction Credit >>>");
    Mono<List<Transaction>> transactionFlux = transactionRepository
        .findByProductId(transaction.getProductId()).collectList();
    return transactionFlux.flatMap(transactionList -> filterTransactionCredit.isSave(transaction));

  }

}
