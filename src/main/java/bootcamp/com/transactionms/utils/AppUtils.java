package bootcamp.com.transactionms.utils;

import bootcamp.com.transactionms.model.Transaction;
import bootcamp.com.transactionms.model.TransactionDto;
import org.springframework.beans.BeanUtils;
import reactor.core.publisher.Mono;

public class AppUtils {
    public static TransactionDto entityToTransactionDto(Transaction transaction){
        TransactionDto transactionDto = new TransactionDto();
        BeanUtils.copyProperties(transaction,transactionDto);
        return transactionDto;
    }
    public static Mono<Transaction> entityToEntity(Transaction transaction , Transaction findTransaction){
        BeanUtils.copyProperties(transaction,findTransaction);
        return Mono.just(findTransaction);
    }
}
