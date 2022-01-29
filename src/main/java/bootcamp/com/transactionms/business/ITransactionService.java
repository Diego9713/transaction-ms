package bootcamp.com.transactionms.business;

import bootcamp.com.transactionms.model.dto.TransactionDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ITransactionService {

  Flux<TransactionDto> findAllTransaction();

  Mono<TransactionDto> findByIdTransaction(String id);

  Flux<TransactionDto> findTransactionByProduct(String productId);

  Flux<TransactionDto> findTransactionByProductAndLimit(String productId);

  Flux<TransactionDto> findCommissionByProduct(String id, String from, String until);

  Mono<TransactionDto> createTransactionDebit(TransactionDto transaction);

  Mono<TransactionDto> createTransferDebit(TransactionDto transaction);

  Mono<TransactionDto> createTransactionCredit(TransactionDto transaction);

  Mono<TransactionDto> updateTransactionDebit(TransactionDto transaction, String id);

  Mono<TransactionDto> updateTransactionCredit(TransactionDto transaction, String id);

  Mono<TransactionDto> removeTransactionDebit(String id);

  Mono<TransactionDto> removeTransactionCredit(String id);


}
