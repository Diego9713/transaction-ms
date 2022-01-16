package bootcamp.com.transactionms.business.helper;

import bootcamp.com.transactionms.model.Transaction;
import bootcamp.com.transactionms.model.TransactionDto;
import bootcamp.com.transactionms.utils.AppUtils;
import bootcamp.com.transactionms.utils.ConstantsPayMethod;
import bootcamp.com.transactionms.utils.ConstantsTransacStatus;
import java.time.LocalDate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class FilterTransaction {
  private static final String BANK = "123456789";

  /**
   * Method that creates the object to save.
   *
   * @param transaction -> is object sending.
   * @return a complete object.
   */
  public Mono<TransactionDto> filterTransactionCreate(TransactionDto transaction) {
    transaction.setTransactionType(transaction.getTransactionType().toUpperCase());
    transaction.setCreatedAt(AppUtils.convertDateToString());
    transaction.setUpdateAt(LocalDate.now());
    transaction.setCommission(0);
    transaction.setStatus(ConstantsTransacStatus.COMPLETE.name());
    if (transaction.getPaymentMethod().equalsIgnoreCase(ConstantsPayMethod.DIRECT.name())) {
      transaction.setFromProduct(BANK);
    }
    return Mono.just(transaction);
  }

  /**
   * Method that creates the object to update.
   *
   * @param findTransaction -> It is wanted object.
   * @return a complete object.
   */
  public Mono<TransactionDto> filterTransactionUpdate(Mono<TransactionDto> findTransaction) {
    TransactionDto otherTransaction = new TransactionDto();
    return findTransaction.flatMap(findTransactions -> {
      otherTransaction.setTransactionType(findTransactions.getTransactionType().toUpperCase());
      otherTransaction.setCommission(findTransactions.getCommission());
      otherTransaction.setPaymentMethod(findTransactions.getPaymentMethod());
      otherTransaction.setCreatedAt(findTransactions.getCreatedAt());
      otherTransaction.setUpdateAt(LocalDate.now());
      otherTransaction.setStatus(ConstantsTransacStatus.COMPLETE.name());
      otherTransaction.setProductId(findTransactions.getProductId());
      otherTransaction.setFromProduct(findTransactions.getFromProduct());
      return Mono.just(otherTransaction);
    });

  }

  /**
   * Method to filter the change of status of the transaction.
   *
   * @param transaction -> is object sending.
   * @return a object transaction.
   */
  public Mono<Transaction> filterTransactionDelete(Transaction transaction) {
    transaction.setStatus(ConstantsTransacStatus.REMOVE.name());
    return Mono.just(transaction);
  }
}
