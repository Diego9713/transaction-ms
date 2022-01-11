package bootcamp.com.transactionms.business.helper;

import bootcamp.com.transactionms.model.Transaction;
import bootcamp.com.transactionms.utils.ConstantsTransacStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Date;

@Component
public class FilterTransaction {
    /**
     * Method that creates the object to save.
     *
     * @param transaction -> is object sending.
     * @return a complete object.
     */
    public Mono<Transaction> filterTransactionCreate(Transaction transaction) {
        transaction.setTransactionType(transaction.getTransactionType().toUpperCase());
        transaction.setCreatedAt(new Date());
        transaction.setUpdateAt(new Date());
        transaction.setStatus(ConstantsTransacStatus.COMPLETE.name());
        return Mono.just(transaction);
    }

    /**
     * Method that creates the object to update.
     *
     * @param findTransaction -> It is wanted object.
     * @return a complete object.
     */
    public Mono<Transaction> filterTransactionUpdate(Mono<Transaction> findTransaction) {
        Transaction otherTransaction = new Transaction();
        return findTransaction.flatMap(findTransactions -> {
            otherTransaction.setTransactionType(findTransactions.getTransactionType().toUpperCase());
            otherTransaction.setUpdateAt(findTransactions.getCreatedAt());
            otherTransaction.setUpdateAt(new Date());
            otherTransaction.setStatus(ConstantsTransacStatus.COMPLETE.name());
            otherTransaction.setProductId(findTransactions.getProductId());
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
