package bootcamp.com.transactionms.business.impl;

import bootcamp.com.transactionms.business.ITransactionCoinPurseService;
import bootcamp.com.transactionms.business.helper.FilterTransaction;
import bootcamp.com.transactionms.business.helper.FilterTransactionCoinPurse;
import bootcamp.com.transactionms.model.dto.CoinPurseDto;
import bootcamp.com.transactionms.model.dto.TransactionDto;
import bootcamp.com.transactionms.model.dto.TransactionDtoBootCoins;
import bootcamp.com.transactionms.repository.ITransactionRepository;
import bootcamp.com.transactionms.utils.AppUtils;
import bootcamp.com.transactionms.utils.ConstantsPayMethod;
import bootcamp.com.transactionms.utils.ConstantsTransacStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service("TransactionCoinPurseService")
@Slf4j
public class TransactionCoinPurseService implements ITransactionCoinPurseService {
  @Autowired
  private ITransactionRepository transactionRepository;
  @Autowired
  private FilterTransactionCoinPurse filterTransactionCoinPurse;
  @Autowired
  private FilterTransaction filterTransaction;

  /**
   * Method to find status transaction pending.
   *
   * @param id -> identified unique transaction.
   * @return objects with status pending.
   */
  @Override
  @Transactional(readOnly = true)
  public Flux<TransactionDto> findTransaccionWithStatus(String id) {
    return transactionRepository.findByProductIdAndStatus(id, ConstantsTransacStatus.PENDING.name())
      .map(AppUtils::entityToTransactionDto);
  }

  /**
   * Method to Create Transaction for coin purse.
   *
   * @param transaction -> object to create.
   * @return object created transaction.
   */
  @Override
  @Transactional
  public Mono<TransactionDto> createTransactionCoinPurse(TransactionDto transaction) {
    log.info("Save Transaction Coin Purse >>>");
    Mono<TransactionDto> filterCreateTransaction = filterTransaction.filterTransactionCreate(transaction);
    Mono<CoinPurseDto> coinPurseFrom = filterTransactionCoinPurse.searchFromCoinPurse(transaction.getFromProduct());
    Mono<CoinPurseDto> coinPurseTo = filterTransactionCoinPurse.searchToCoinPurse(transaction.getProductId());

    return coinPurseFrom.flatMap(c -> filterTransactionCoinPurse.filterCoinPurse(c, transaction))
      .filter(Boolean.TRUE::equals)
      .flatMap(b -> coinPurseTo.flatMap(c -> filterTransactionCoinPurse.filterCoinPurse(c, transaction)))
      .filter(Boolean.TRUE::equals)
      .flatMap(b -> filterCreateTransaction.map(AppUtils::transactionDtoToEntity).flatMap(transactionRepository::save))
      .map(AppUtils::entityToTransactionDto);

  }

  /**
   * Method to Buy Bootcoins of coin purse.
   *
   * @param transaction -> object to create.
   * @return object created transaction.
   */
  @Override
  @Transactional
  public Mono<TransactionDtoBootCoins> buyBootCoins(TransactionDto transaction) {
    return filterTransaction.filterTransactionCreate(transaction)
      .map(AppUtils::transactionDtoToEntity)
      .flatMap(transactionRepository::save)
      .map(AppUtils::entityToBootCoins);
  }

  /**
   * Method to update transaction for coin purse.
   *
   * @param id          -> identify unique of transaction.
   * @param transaction -> object to create.
   * @return object updated.
   */
  @Override
  @Transactional
  public Mono<TransactionDto> updateTransactionCoinPurse(TransactionDto transaction, String id) {
    log.info("Save Transaction Coin Purse >>>");
    double amout = transaction.getTransactionAmount();
    Mono<TransactionDto> findTransaction = transactionRepository.findById(id).map(AppUtils::entityToTransactionDto);
    Mono<TransactionDto> newFindTransaction = findTransaction
        .flatMap(transactionDto -> filterTransactionCoinPurse.filterUpdateTransaction(transactionDto, transaction));
    Mono<CoinPurseDto> coinPurseFrom = filterTransactionCoinPurse.searchFromCoinPurse(transaction.getFromProduct());
    Mono<CoinPurseDto> coinPurseTo = filterTransactionCoinPurse.searchToCoinPurse(transaction.getProductId());

    return newFindTransaction.flatMap(transactionDto ->
        coinPurseFrom.flatMap(c -> filterTransactionCoinPurse.filterCoinPurse(c, transactionDto))
          .filter(Boolean.TRUE::equals)
          .flatMap(b -> coinPurseTo.flatMap(c -> filterTransactionCoinPurse.filterCoinPurse(c, transactionDto)))
          .filter(Boolean.TRUE::equals)
          .flatMap(b -> filterTransactionCoinPurse.setAttributeTransaction(transactionDto, amout)))
      .map(AppUtils::transactionDtoToEntity)
      .flatMap(transactionRepository::save)
      .map(AppUtils::entityToTransactionDto);
  }

  /**
   * Method to remove transaction of coin purse.
   *
   * @param id -> identify unique of transaction.
   * @return object change status.
   */
  @Override
  @Transactional
  public Mono<TransactionDto> removeTransactionCoinPurse(String id) {
    Mono<TransactionDto> findTransaction = transactionRepository.findById(id).map(AppUtils::entityToTransactionDto);
    return findTransaction.flatMap(transactionDto -> filterTransactionCoinPurse.removeTransaction(transactionDto)
        .filter(Boolean.TRUE::equals)
        .flatMap(bool -> filterTransactionCoinPurse.changeStatusTransaction(transactionDto)))
      .map(AppUtils::transactionDtoToEntity)
      .flatMap(transactionRepository::save)
      .map(AppUtils::entityToTransactionDto);
  }
}
