package bootcamp.com.transactionms.business.impl;

import bootcamp.com.transactionms.business.ITransactionService;
import bootcamp.com.transactionms.business.helper.FilterTransaction;
import bootcamp.com.transactionms.business.helper.FilterTransactionCredit;
import bootcamp.com.transactionms.business.helper.FilterTransactionDebit;
import bootcamp.com.transactionms.model.Transaction;
import bootcamp.com.transactionms.model.dto.TransactionDto;
import bootcamp.com.transactionms.repository.ITransactionRepository;
import bootcamp.com.transactionms.utils.AppUtils;
import bootcamp.com.transactionms.utils.ConstantsTransacStatus;
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
    log.info("FindAll Transaction>>>");
    return transactionRepository.findAll()
      .filter(transaction -> transaction.getStatus().equalsIgnoreCase(ConstantsTransacStatus.COMPLETE.name()))
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
   * Method to find the  ten movements that a product.
   *
   * @param productId -> identifier of la account.
   * @return a list transaction.
   */
  @Override
  @Transactional(readOnly = true)
  public Flux<TransactionDto> findTransactionByProductAndLimit(String productId) {
    log.info("Find transactionByProduct for limit >>>");
    return transactionRepository.findByProductIdOrderByTransactionAmountDesc(productId)
      .filter(transaction -> transaction.getStatus().equalsIgnoreCase(ConstantsTransacStatus.COMPLETE.name()))
      .map(AppUtils::entityToTransactionDto);
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
    log.info("Find transaction for Commission >>>");
    return transactionRepository.findByProductIdAndCreatedAtBetween(productId, fromDate, untilDate)
      .filter(transaction -> transaction.getStatus().equalsIgnoreCase(ConstantsTransacStatus.COMPLETE.name()))
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
    Flux<Transaction> transactionFlux = transactionRepository.findByProductId(transaction.getProductId());

    Mono<TransactionDto> filterTransactionDtoMono = filterCreateTransaction
        .flatMap(findTransaction -> filterTransactionDebit
        .filterDebit(findTransaction, transaction.getProductId(), transactionFlux));

    return filterTransactionDtoMono
      .filter(transactionDto -> transactionDto.getProductId() != null)
      .map(AppUtils::transactionDtoToEntity)
      .flatMap(findTransaction -> transactionRepository.save(findTransaction))
      .map(AppUtils::entityToTransactionDto);
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
    Flux<Transaction> transactionFluxTo = transactionRepository.findByProductId(transaction.getProductId());
    Flux<Transaction> transactionFluxFrom = transactionRepository.findByProductId(transaction.getFromProduct());

    Mono<TransactionDto> transfer = filterCreateTransaction
        .flatMap(t -> filterTransactionDebit.filterDebit(t, transaction.getProductId(), transactionFluxTo)
        .filter(afterTransaction -> afterTransaction.getProductId() != null)
        .flatMap(next -> filterTransactionDebit.filterDebit(t, transaction.getFromProduct(), transactionFluxFrom)));

    return transfer.filter(t -> t.getProductId() != null)
      .map(AppUtils::transactionDtoToEntity)
      .flatMap(transactionRepository::save)
      .map(AppUtils::entityToTransactionDto);
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
    double amount = transaction.getTransactionAmount();
    Mono<TransactionDto> findTransaction = transactionRepository.findById(id).map(AppUtils::entityToTransactionDto);
    Flux<Transaction> transactionFlux = transactionRepository.findByProductId(transaction.getProductId());

    Mono<TransactionDto> filterProduct = findTransaction
        .switchIfEmpty(Mono.empty())
        .flatMap(next -> {
          transaction.setId(id);
          TransactionDto transactionDto = filterTransactionDebit.updateDebits(transaction, next);
          return filterTransactionDebit.filterDebit(transactionDto, transactionDto.getProductId(), transactionFlux);
        });

    return filterProduct
      .filter(transactionDto -> transactionDto.getId() != null)
      .flatMap(transactionDto -> filterTransaction.filterTransactionUpdate(findTransaction)
        .map(AppUtils::transactionDtoToEntity)
        .flatMap(transactionModel -> {
          transactionModel.setId(id);
          transactionModel.setTransactionAmount(amount);
          return transactionRepository.save(transactionModel);
        })).map(AppUtils::entityToTransactionDto);
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
    return transactionRepository.findById(id)
      .switchIfEmpty(Mono.empty())
      .filter(transaction -> transaction.getStatus()
        .equalsIgnoreCase(ConstantsTransacStatus.COMPLETE.name()))
      .flatMap(transaction -> filterTransactionDebit.filterRemoveProduct(transaction)
        .filter(Boolean.TRUE::equals)
        .flatMap(opt -> filterTransaction.filterTransactionDelete(transaction))
        .flatMap(transactionRepository::save)
        .map(AppUtils::entityToTransactionDto));
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
    Flux<Transaction> transactionFlux = transactionRepository.findByProductId(transaction.getProductId());
    Mono<TransactionDto> transactionDtoMono = filterTransactionCredit.filterCredit(transaction, transactionFlux);

    return transactionDtoMono
      .filter(transactionDto -> transactionDto.getProductId() != null)
      .flatMap(transactionDto -> filterTransaction.filterTransactionCreate(transaction))
      .map(AppUtils::transactionDtoToEntity)
      .flatMap(transactionRepository::save)
      .map(AppUtils::entityToTransactionDto);
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
    double amount = transaction.getTransactionAmount();
    Mono<TransactionDto> findTransaction = transactionRepository.findById(id).map(AppUtils::entityToTransactionDto);
    Flux<Transaction> transactionFlux = transactionRepository.findByProductId(transaction.getProductId());

    Mono<TransactionDto> transactionDtoMono = findTransaction
        .switchIfEmpty(Mono.empty())
        .flatMap(newTransaction -> {
          transaction.setId(id);
          TransactionDto transactionDto = filterTransactionCredit.updateCredits(transaction, newTransaction);
          return filterTransactionCredit.filterCredit(transactionDto, transactionFlux);
        });

    return transactionDtoMono
      .filter(transactionDto -> transactionDto.getId() != null)
      .flatMap(transactionDto -> filterTransaction.filterTransactionUpdate(findTransaction))
      .map(AppUtils::transactionDtoToEntity)
      .flatMap(transactionModel -> {
        transactionModel.setId(id);
        transactionModel.setTransactionAmount(amount);
        return transactionRepository.save(transactionModel);
      }).map(AppUtils::entityToTransactionDto);
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
        .filter(Boolean.TRUE::equals)
        .flatMap(opt -> filterTransaction.filterTransactionDelete(transaction))
        .flatMap(transactionRepository::save)
        .map(AppUtils::entityToTransactionDto));
  }

}
