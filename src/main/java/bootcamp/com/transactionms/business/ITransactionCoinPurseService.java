package bootcamp.com.transactionms.business;

import bootcamp.com.transactionms.model.dto.TransactionDto;
import bootcamp.com.transactionms.model.dto.TransactionDtoBootCoins;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ITransactionCoinPurseService {

  Flux<TransactionDto> findTransaccionWithStatus(String id);

  Mono<TransactionDto> createTransactionCoinPurse(TransactionDto transaction);

  Mono<TransactionDtoBootCoins> buyBootCoins(TransactionDto transaction);

  Mono<TransactionDto> updateTransactionCoinPurse(TransactionDto transaction, String id);

  Mono<TransactionDto> removeTransactionCoinPurse(String id);
}
