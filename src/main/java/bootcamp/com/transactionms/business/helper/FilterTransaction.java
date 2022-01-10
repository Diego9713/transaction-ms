package bootcamp.com.transactionms.business.helper;
import bootcamp.com.transactionms.model.Transaction;
import bootcamp.com.transactionms.utils.ConstantsTransacStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.Date;

@Component
public class FilterTransaction {

    public Mono<Transaction> filterTransactionCreate(Transaction transaction){
       transaction.setTransactionType(transaction.getTransactionType().toUpperCase());
       transaction.setCreatedAt(new Date());
       transaction.setUpdateAt(new Date());
       transaction.setStatus(ConstantsTransacStatus.COMPLETE.name());
       return Mono.just(transaction);
    }
    public Mono<Transaction> filterTransactionUpdate(Mono<Transaction> findTransaction){
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
    public Mono<Transaction> filterTransactionDelete(Transaction transaction){
        transaction.setStatus(ConstantsTransacStatus.REMOVE.name());
        return Mono.just(transaction);
    }
}
