package bootcamp.com.transactionms.business.impl;

import bootcamp.com.transactionms.business.helper.FilterTransaction;
import bootcamp.com.transactionms.business.helper.FilterTransactionCoinPurse;
import bootcamp.com.transactionms.model.Transaction;
import bootcamp.com.transactionms.model.dto.CoinPurseDto;
import bootcamp.com.transactionms.model.dto.TransactionDto;
import bootcamp.com.transactionms.repository.ITransactionRepository;
import bootcamp.com.transactionms.utils.ConstantsTransacStatus;
import org.junit.jupiter.api.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.when;

@SpringBootTest
class TransactionCoinPurseServiceTest {
  @Autowired
  private TransactionCoinPurseService transactionCoinPurseService;
  @MockBean
  private FilterTransactionCoinPurse filterTransactionCoinPurse;
  @MockBean
  private FilterTransaction filterTransaction;
  @MockBean
  private ITransactionRepository transactionRepository;

  private static final TransactionDto transactionDto = new TransactionDto();
  private static final Transaction transaction = new Transaction();
  private static final Transaction transactionRemove = new Transaction();
  private static final List<Transaction> transactionDtoList = new ArrayList<>();
  private static final CoinPurseDto coinPurseDtoTo = new CoinPurseDto();
  private static final CoinPurseDto coinPurseDtoFrom = new CoinPurseDto();

  /**** Product *****/
  private static final String id = "61db9bc2b09be072956ae684";
  private static final String productId = "61db64d731dec743727907f3";
  private static final String fromProduct = "61db64ee31dec743727907f4";
  private static final String paymentMethod = "COIN_PURSE";
  private static final String transactionType = "TRANSFER";
  private static final double transactionAmount = 250;
  private static final double commission = 0;
  private static final String createdAt = "2022-01-14";
  private static final String createdBy = "pedro";
  private static final LocalDate updateAt = LocalDate.now();
  private static final String updateBy = "pedro";
  private static final String status = "COMPLETE";

  /**** Coin Purse *****/
  private static final String idCoinTo = "61db64d731dec743727907f3" ;
  private static final String idCoinFrom = "61db64ee31dec743727907f4" ;
  private static final String documentType = "DNI";
  private static final String documentNumber = "785412365";
  private static final String coinPurseType = "COIN_PURSE_DNI";
  private static final String firstName = "Diego";
  private static final String lastName = "Tafur Sanchez";
  private static final String phoneNumber = "85479632";
  private static final String phoneImei = "MXTYD98563";
  private static final String email = "tafur232@gmail.com";
  private static final String accountNumber = null;
  private static final double amount = 5000;
  private static final String statusCoin = "ACTIVE";

  @BeforeAll
  static void setUp(){
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

    coinPurseDtoTo.setId(idCoinTo);
    coinPurseDtoTo.setDocumentType(documentType);
    coinPurseDtoTo.setDocumentNumber(documentNumber);
    coinPurseDtoTo.setCoinPurseType(coinPurseType);
    coinPurseDtoTo.setFirstName(firstName);
    coinPurseDtoTo.setLastName(lastName);
    coinPurseDtoTo.setPhoneNumber(phoneNumber);
    coinPurseDtoTo.setPhoneImei(phoneImei);
    coinPurseDtoTo.setEmail(email);
    coinPurseDtoTo.setAccountNumber(accountNumber);
    coinPurseDtoTo.setAmount(amount);
    coinPurseDtoTo.setStatus(statusCoin);

    BeanUtils.copyProperties(coinPurseDtoTo, coinPurseDtoFrom);
    coinPurseDtoFrom.setId(idCoinFrom);
    BeanUtils.copyProperties(transactionDto, transaction);
    BeanUtils.copyProperties(transactionDto, transactionRemove);
    transactionRemove.setStatus(ConstantsTransacStatus.REMOVE.name());
    transactionDtoList.add(transaction);
  }


  @Test
  void createTransactionCoinPurse() {
    when(filterTransaction.filterTransactionCreate(transactionDto)).thenReturn(Mono.just(transactionDto));
    when(filterTransactionCoinPurse.searchFromCoinPurse(transactionDto.getFromProduct())).thenReturn(Mono.just(coinPurseDtoFrom));
    when(filterTransactionCoinPurse.searchToCoinPurse(transactionDto.getProductId())).thenReturn(Mono.just(coinPurseDtoTo));
    when(filterTransactionCoinPurse.filterCoinPurse(coinPurseDtoFrom,transactionDto)).thenReturn(Mono.just(true));
    when(filterTransactionCoinPurse.filterCoinPurse(coinPurseDtoTo,transactionDto)).thenReturn(Mono.just(true));
    when(transactionRepository.save(transaction)).thenReturn(Mono.just(transaction));
    Assertions.assertNotNull(transactionCoinPurseService.createTransactionCoinPurse(transactionDto));

  }

  @Test
  void updateTransactionCoinPurse() {
  }

  @Test
  void removeTransactionCoinPurse() {
  }
}