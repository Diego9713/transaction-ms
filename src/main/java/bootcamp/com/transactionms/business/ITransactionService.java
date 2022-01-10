package bootcamp.com.transactionms.business;

import bootcamp.com.transactionms.model.Transaction;
import bootcamp.com.transactionms.model.TransactionDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ITransactionService {

    Flux<TransactionDto> findAllTransaction();

    Mono<TransactionDto> findByIdTransaction(String id);

    Flux<TransactionDto> findTransactionByProduct(String productId);

    Mono<TransactionDto> createTransactionDebit(Transaction transaction);

    Mono<TransactionDto> createTransactionCredit(Transaction transaction);

    Mono<TransactionDto> updateTransactionDebit(Transaction transaction, String id);

    Mono<TransactionDto> updateTransactionCredit(Transaction transaction, String id);

    Mono<TransactionDto> removeTransactionDebit(String id);

    Mono<TransactionDto> removeTransactionCredit(String id);
}
