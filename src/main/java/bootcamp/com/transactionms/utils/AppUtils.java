package bootcamp.com.transactionms.utils;

import bootcamp.com.transactionms.model.Transaction;
import bootcamp.com.transactionms.model.dto.TransactionDto;
import bootcamp.com.transactionms.model.dto.TransactionDtoBootCoins;
import java.time.LocalDate;
import org.springframework.beans.BeanUtils;

public class AppUtils {

  private AppUtils() {
  }

  /**
   * Method to modify the return of data.
   *
   * @param transaction -> transaction object with entered data.
   * @return object modified.
   */
  public static TransactionDto entityToTransactionDto(Transaction transaction) {
    TransactionDto transactionDto = new TransactionDto();
    BeanUtils.copyProperties(transaction, transactionDto);
    return transactionDto;
  }

  /**
   * Method to modify the return of data.
   *
   * @param transactionDto -> transaction object with entered data.
   * @return object modified.
   */
  public static Transaction transactionDtoToEntity(TransactionDto transactionDto) {
    Transaction transaction = new Transaction();
    BeanUtils.copyProperties(transactionDto, transaction);
    return transaction;
  }

  /**
   * Method to modify the return of data.
   *
   * @param transaction -> transaction object with entered data.
   * @return object modified.
   */
  public static TransactionDtoBootCoins entityToBootCoins(Transaction transaction) {
    TransactionDtoBootCoins transactionDtoBootCoins = new TransactionDtoBootCoins();
    BeanUtils.copyProperties(transaction, transactionDtoBootCoins);
    return transactionDtoBootCoins;
  }

  /**
   * Method to convert date in date string.
   *
   * @return a string object date.
   */
  public static String convertDateToString() {
    String date = LocalDate.now().toString();
    String[] listDate = date.split("T");
    return listDate[0];
  }

  /**
   * Method to convert String in double.
   *
   * @return a double object amount.
   */
  public static double convertStringToDouble(String amount) {
    return Double.parseDouble(amount);
  }
}
