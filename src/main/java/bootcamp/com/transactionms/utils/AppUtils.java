package bootcamp.com.transactionms.utils;

import bootcamp.com.transactionms.model.Transaction;
import bootcamp.com.transactionms.model.TransactionDto;
import org.springframework.beans.BeanUtils;

public class AppUtils {
    /**
     * Method to modify the return of data.
     *
     * @param transaction -> transaction object with entered data.
     * @return object modified.
     */
    public static TransactionDto entityToTransactionDto(Transaction transaction){
        TransactionDto transactionDto = new TransactionDto();
        BeanUtils.copyProperties(transaction,transactionDto);
        return transactionDto;
    }


}
