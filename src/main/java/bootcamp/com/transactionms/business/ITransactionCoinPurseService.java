package bootcamp.com.transactionms.business;

import bootcamp.com.transactionms.model.dto.TransactionDto;
import reactor.core.publisher.Mono;

public interface ITransactionCoinPurseService {

  Mono<TransactionDto> createTransactionCoinPurse(TransactionDto transaction);

  Mono<TransactionDto> updateTransactionCoinPurse(TransactionDto transaction, String id);

  Mono<TransactionDto> removeTransactionCoinPurse(String id);
}
