package bootcamp.com.transactionms.business.helper;

import bootcamp.com.transactionms.model.Transaction;
import bootcamp.com.transactionms.model.TransactionDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilterTransactionTest {

  @Autowired
  private FilterTransaction filterTransaction;

  private static final TransactionDto transactionDto = new TransactionDto();
  private static final Transaction transaction = new Transaction();
  private static final List<Transaction> transactionDtoList = new ArrayList<>();
  private static final String id = "61db9bc2b09be072956ae684";
  private static final String productId = "61db64d731dec743727907f3";
  private static final String fromProduct = "61db64ee31dec743727907f4";
  private static final String paymentMethod = "DIRECT";
  private static final String transactionType = "DEPOSIT";
  private static final double transactionAmount = 250;
  private static final double commission = 0;
  private static final String createdAt = "2022-01-14";
  private static final String createdBy = "pedro";
  private static final LocalDate updateAt = LocalDate.now();
  private static final String updateBy = "pedro";
  private static final String status = "COMPLETE";

  @BeforeAll
  static void setUp() {
    transactionDto.setId(id);
    transactionDto.setProductId(productId);
    transactionDto.setFromProduct(fromProduct);
    transactionDto.setPaymentMethod(paymentMethod);
    transactionDto.setTransactionType(transactionType);
    transactionDto.setTransactionAmount(transactionAmount);
    transactionDto.setCommission(commission);
    transactionDto.setCreatedAt(createdAt);
    transactionDto.setCreatedBy(createdBy);
    transactionDto.setUpdateAt(updateAt);
    transactionDto.setUpdateBy(updateBy);
    transactionDto.setStatus(status);
    BeanUtils.copyProperties(transactionDto, transaction);
    transactionDtoList.add(transaction);
  }

  @Test
  void filterTransactionCreate() {
    Assertions.assertNotNull(filterTransaction.filterTransactionCreate(transactionDto));
  }

  @Test
  void filterTransactionUpdate() {
    Assertions.assertNotNull(filterTransaction.filterTransactionUpdate(Mono.just(transactionDto)));
  }

  @Test
  void filterTransactionDelete() {
    Assertions.assertNotNull(filterTransaction.filterTransactionDelete(transaction));
  }
}