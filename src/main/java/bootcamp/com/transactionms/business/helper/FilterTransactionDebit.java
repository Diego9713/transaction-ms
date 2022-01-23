package bootcamp.com.transactionms.business.helper;

import bootcamp.com.transactionms.model.Transaction;
import bootcamp.com.transactionms.model.dto.ProductDto;
import bootcamp.com.transactionms.model.dto.TransactionDto;
import bootcamp.com.transactionms.utils.ConstantsCredit;
import bootcamp.com.transactionms.utils.ConstantsDebit;
import bootcamp.com.transactionms.utils.ConstantsDebitTransac;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class FilterTransactionDebit {
  @Autowired
  private WebClientProductHelper webClientProductHelper;
  private static final double COMMISSION = 5.00;

  /**
   * Method to configure the saving of the transaction.
   *
   * @param transaction     -> is object sending.
   * @param transactionFlux -> is the number of transactions carried out.
   * @return a object product saving.
   */
  public Mono<TransactionDto> isSave(TransactionDto transaction, String id, List<Transaction> transactionFlux) {
    Mono<ProductDto> findToProduct = webClientProductHelper.findProduct(id);
    return findToProduct.filter(e -> Arrays.stream(ConstantsDebit.values())
        .anyMatch(m -> m.toString().equalsIgnoreCase(e.getAccountType())))
      .switchIfEmpty(Mono.just(new ProductDto()))
      .flatMap(productDto -> isTypeTransact(transaction, productDto, transactionFlux));
  }

  /**
   * Method to condition the transaction amount.
   *
   * @param transaction     -> is object sending.
   * @param productDto      -> is object find.
   * @param transactionFlux -> is list transactions.
   * @return a product condition.
   */
  public Mono<TransactionDto> isTypeTransact(TransactionDto transaction,
                                             ProductDto productDto,
                                             List<Transaction> transactionFlux) {
    Mono<TransactionDto> transactionMono = null;
    if (transaction.getId() == null
        && !transaction.getTransactionType().equalsIgnoreCase(ConstantsDebitTransac.TRANSFER.name())) {
      Flux<ProductDto> productDtoFlux = webClientProductHelper.findProductByAccount(productDto.getAccountNumber());
      Mono<List<ProductDto>> productDtoList = productDtoFlux.collectList();
      transactionMono = productDtoList
        .flatMap(productDos -> filterProductByLevel(transaction, transactionFlux, productDos));
    } else {
      transactionMono = filterProductByTransaction(transaction, productDto, transactionFlux);
    }
    return transactionMono;
  }

  /**
   * Method filter product for level of creation.
   *
   * @param transaction     -> is object sending.
   * @param transactionFlux -> is list transactions.
   * @param productDtoList  -> is list of account product.
   * @return a product filter.
   */
  public Mono<TransactionDto> filterProductByLevel(TransactionDto transaction,
                                                   List<Transaction> transactionFlux,
                                                   List<ProductDto> productDtoList) {
    Mono<TransactionDto> transactionMono = Mono.just(new TransactionDto());
    for (ProductDto dto : productDtoList) {
      if (dto.getAmount() >= transaction.getTransactionAmount()
          && !dto.getAccountType().equalsIgnoreCase(ConstantsCredit.CREDIT.name())) {
        transactionMono = filterProductByTransaction(transaction, dto, transactionFlux);
        break;
      }
    }
    return transactionMono;
  }

  /**
   * Method to filter product for transaction type.
   *
   * @param transaction     -> is object sending.
   * @param productDto      -> is object find.
   * @param transactionFlux -> is list transactions.
   * @return a product for transaction.
   */
  public Mono<TransactionDto> filterProductByTransaction(TransactionDto transaction,
                                                         ProductDto productDto,
                                                         List<Transaction> transactionFlux) {

    Mono<TransactionDto> transactionMono = Mono.just(new TransactionDto());

    if (Arrays.stream(ConstantsDebitTransac.values()).anyMatch(m -> m.toString()
        .equalsIgnoreCase(transaction.getTransactionType()))) {

      if (transaction.getTransactionType().equalsIgnoreCase(ConstantsDebitTransac.DEPOSIT.name())) {

        productDto.setAmount(productDto.getAmount() + transaction.getTransactionAmount());

        transactionMono = filterSaveProduct(productDto, transactionFlux, transaction);
      } else if (transaction.getTransactionType().equalsIgnoreCase(ConstantsDebitTransac.WITHDRAWAL.name())) {
        if (transaction.getId() == null) {
          if (productDto.getAmount() > 0 && productDto.getAmount() >= transaction.getTransactionAmount()) {

            productDto.setAmount(productDto.getAmount() - transaction.getTransactionAmount());
            transactionMono = filterSaveProduct(productDto, transactionFlux, transaction);

          }
        } else {

          productDto.setAmount(productDto.getAmount() + transaction.getTransactionAmount());
          transactionMono = filterSaveProduct(productDto, transactionFlux, transaction);

        }
      } else {
        transactionMono = filterProductTransfer(transaction, productDto, transactionFlux);
      }

    }
    return transactionMono;
  }

  /**
   * Method to generate transfer.
   *
   * @param transaction     -> is object sending.
   * @param productDto      -> is object find.
   * @param transactionFlux -> is list transactions.
   * @return a product condition.
   */
  public Mono<TransactionDto> filterProductTransfer(TransactionDto transaction,
                                                    ProductDto productDto,
                                                    List<Transaction> transactionFlux) {
    Mono<TransactionDto> transactionMono;

    if (transaction.getFromProduct().equalsIgnoreCase(productDto.getId())) {
      productDto.setAmount(productDto.getAmount() - transaction.getTransactionAmount());
      transactionMono = filterSaveProduct(productDto, transactionFlux, transaction);
    } else {
      productDto.setAmount(productDto.getAmount() + transaction.getTransactionAmount());
      transactionMono = saveProduct(productDto, transaction);
    }
    return transactionMono;
  }

  /**
   * Method that filters the storage of the product.
   *
   * @param productDto      -> is object find.
   * @param transactionFlux -> is list transactions.
   * @param transaction     -> is object sending.
   * @return a product condition.
   */
  public Mono<TransactionDto> filterSaveProduct(ProductDto productDto,
                                                List<Transaction> transactionFlux,
                                                TransactionDto transaction) {
    Mono<TransactionDto> transactionMono = Mono.just(new TransactionDto());

    if (productDto.getAccountType().equalsIgnoreCase(ConstantsDebit.SAVING.name())) {
      transactionMono = accountTypeSaving(productDto, transactionFlux, transaction);
    }

    if (productDto.getAccountType().equalsIgnoreCase(ConstantsDebit.CURRENT.name())) {
      transactionMono = accountTypeCurrent(productDto, transactionFlux, transaction);
    }
    if (productDto.getAccountType().equalsIgnoreCase(ConstantsDebit.FIXED_TERM.name())) {
      transactionMono = accountTypeFixedTerm(productDto, transactionFlux, transaction);
    }
    return transactionMono;
  }

  /**
   * Filtering method by type of saving account.
   *
   * @param productDto      -> is object find.
   * @param transactionFlux -> is list transactions.
   * @param transaction     -> is object sending.
   * @return a product condition.
   */
  public Mono<TransactionDto> accountTypeSaving(ProductDto productDto,
                                                List<Transaction> transactionFlux,
                                                TransactionDto transaction) {
    Mono<TransactionDto> transactionMono;
    if (productDto.getCreatedAt().getMonthValue() != LocalDate.now().getMonthValue() && transaction.getId() == null) {
      productDto.setCreatedAt(LocalDate.now());
      productDto.setMaxTransactNumber(productDto.getMaxTransactNumber() + 10);
    }
    if (transactionFlux.size() >= productDto.getMaxTransactNumber() && transaction.getId() == null) {
      productDto.setAmount(productDto.getAmount() - COMMISSION);
      transaction.setCommission(COMMISSION);
    }
    transactionMono = saveProduct(productDto, transaction);
    return transactionMono;
  }

  /**
   * Filtering method by type of current account.
   *
   * @param productDto      -> is object find.
   * @param transactionFlux -> is list transactions.
   * @param transaction     -> is object sending.
   * @return a product condition.
   */
  public Mono<TransactionDto> accountTypeCurrent(ProductDto productDto,
                                                 List<Transaction> transactionFlux,
                                                 TransactionDto transaction) {
    final Mono<TransactionDto> transactionMono;

    if (productDto.getCreatedAt().getMonthValue() != LocalDate.now().getMonthValue() && transaction.getId() == null) {
      productDto.setCreatedAt(LocalDate.now());
      productDto.setMaxTransactNumber(productDto.getMaxTransactNumber() + 10);
    }
    if (LocalDateTime.now().isEqual(productDto.getMaintenanceCommissionDay()) && transaction.getId() == null) {
      productDto.setMaintenanceCommissionDay(LocalDateTime.now().plusMonths(1));
      productDto.setAmount(productDto.getAmount() - productDto.getMaintenanceCommission());
      transaction.setCommission(transaction.getCommission() + productDto.getMaintenanceCommission());
    }
    if (transactionFlux.size() >= productDto.getMaxTransactNumber() && transaction.getId() == null) {
      productDto.setAmount(productDto.getAmount() - COMMISSION);
      transaction.setCommission(transaction.getCommission() + COMMISSION);
    }
    transactionMono = saveProduct(productDto, transaction);
    return transactionMono;
  }

  /**
   * Filtering method by type of fixed term account.
   *
   * @param productDto      -> is object find.
   * @param transactionFlux -> is list transactions.
   * @param transaction     -> is object sending.
   * @return a product condition.
   */
  public Mono<TransactionDto> accountTypeFixedTerm(ProductDto productDto,
                                                   List<Transaction> transactionFlux,
                                                   TransactionDto transaction) {

    Mono<TransactionDto> transactionMono = Mono.just(new TransactionDto());
    if (productDto.getCreatedAt().getMonthValue() != LocalDate.now().getMonthValue() && transaction.getId() == null) {
      productDto.setCreatedAt(LocalDate.now());
      productDto.setMaxTransactNumber(productDto.getMaxTransactNumber() + 1);
    }
    if (transactionFlux.size() < productDto.getMaxTransactNumber() && LocalDate.now()
        .isEqual(productDto.getTransactNumberDay()) || transaction.getId() != null) {
      if (transaction.getId() == null) {
        productDto.setTransactNumberDay(LocalDate.now().plusDays(10));
      }
      transactionMono = saveProduct(productDto, transaction);
    }
    return transactionMono;
  }

  /**
   * Method to save a product from transaction.
   *
   * @param productDto  -> is object find.
   * @param transaction -> is object sending.
   * @return a product condition.
   */
  public Mono<TransactionDto> saveProduct(ProductDto productDto, TransactionDto transaction) {
    Mono<ProductDto> productDtoMono = webClientProductHelper.updateProduct(productDto.getId(), productDto);
    transaction.setProductId(productDto.getId());
    return productDtoMono.flatMap(productDto1 -> productDto1.getId() != null
      ? Mono.just(transaction)
      : Mono.just(new TransactionDto()));
  }

  /**
   * Method that filters the remove of the product.
   *
   * @param transaction -> is object sending.
   * @return a condition of remove.
   */
  public Mono<Boolean> filterRemoveProduct(Transaction transaction) {
    Mono<Boolean> optionRemove;
    Mono<ProductDto> findToProduct = webClientProductHelper.findProduct(transaction.getProductId());
    if (transaction.getTransactionType().equalsIgnoreCase(ConstantsDebitTransac.DEPOSIT.name())) {
      optionRemove = findToProduct.flatMap(productDto -> {
        productDto.setAmount(productDto.getAmount() - transaction.getTransactionAmount());
        return webClientProductHelper.updateProduct(productDto.getId(), productDto)
          .flatMap(productDto1 -> Mono.just(true));
      });
    } else {
      optionRemove = findToProduct.flatMap(productDto -> {
        productDto.setAmount(productDto.getAmount() + transaction.getTransactionAmount());
        return webClientProductHelper.updateProduct(productDto.getId(), productDto)
          .flatMap(productDto1 -> Mono.just(true));
      });
    }

    return optionRemove;
  }

  /**
   * Method to condition the saving of the transaction Debits.
   *
   * @param transaction -> attribute object type transaction.
   * @return the condition for saving.
   */
  public Mono<TransactionDto> filterDebit(TransactionDto transaction, String id, Flux<Transaction> transactionFlux) {
    Mono<List<Transaction>> transactionMono = transactionFlux.collectList();
    return transactionMono.flatMap(transactionList -> isSave(transaction, id, transactionList));
  }

  /**
   * Method to condition the updating of the transaction Debits.
   *
   * @param transaction     -> attribute object type transaction.
   * @param findTransaction -> transaction finder.
   * @return the condition for saving.
   */
  public TransactionDto updateDebits(TransactionDto transaction, TransactionDto findTransaction) {
    if (findTransaction.getTransactionType().equalsIgnoreCase(ConstantsDebitTransac.DEPOSIT.name())) {
      transaction.setTransactionAmount(findTransaction.getTransactionAmount() + transaction.getTransactionAmount());
    } else {
      if (findTransaction.getTransactionAmount() > transaction.getTransactionAmount()) {
        transaction.setTransactionAmount(findTransaction.getTransactionAmount() - transaction.getTransactionAmount());
      } else {
        transaction.setTransactionAmount(transaction.getTransactionAmount() - findTransaction.getTransactionAmount());
      }
    }
    return transaction;
  }



}
