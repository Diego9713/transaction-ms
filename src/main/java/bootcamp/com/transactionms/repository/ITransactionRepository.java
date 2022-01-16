package bootcamp.com.transactionms.repository;

import bootcamp.com.transactionms.model.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ITransactionRepository extends ReactiveMongoRepository<Transaction, String> {
  Flux<Transaction> findByProductId(String id);

  Flux<Transaction> findByProductIdAndCreatedAtBetween(String id, String from, String until);
}
