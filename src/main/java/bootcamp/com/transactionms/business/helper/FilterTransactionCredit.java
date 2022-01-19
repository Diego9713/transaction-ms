package bootcamp.com.transactionms.business.helper;

import bootcamp.com.transactionms.model.ProductDto;
import bootcamp.com.transactionms.model.Transaction;
import bootcamp.com.transactionms.model.TransactionDto;
import bootcamp.com.transactionms.utils.ConstantsCredit;
import bootcamp.com.transactionms.utils.ConstantsCreditTransac;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class FilterTransactionCredit {
  @Autowired
  private WebClientProductHelper webClientProductHelper;

  /**
   * Method to configure the saving of the transaction.
   *
   * @param transaction is object sending.
   * @return a object product saving.
   */

  public Mono<ProductDto> isSave(TransactionDto transaction) {
    Mono<ProductDto> findToProduct = webClientProductHelper.findProduct(transaction.getProductId());
    return findToProduct.filter(e -> e.getAccountType()
        .equalsIgnoreCase(ConstantsCredit.CREDIT.name()))
      .switchIfEmpty(Mono.just(new ProductDto()))
      .flatMap(productDto -> isTypeTransfer(transaction, productDto));
  }

  /**
   * Method to condition the transaction amount.
   *
   * @param transaction -> is object sending
   * @param productDto  -> is object find.
   * @return a product condition.
   */
  public Mono<ProductDto> isTypeTransfer(TransactionDto transaction, ProductDto productDto) {
    Mono<ProductDto> productDtoMono = Mono.just(new ProductDto());

    if (Arrays.stream(ConstantsCreditTransac.values()).anyMatch(m -> m.toString()
        .equalsIgnoreCase(transaction.getTransactionType()))) {
      if (transaction.getTransactionType().equalsIgnoreCase(ConstantsCreditTransac.CREDIT_PAYMENT.name())) {
        if (productDto.getAmount() > 0
            && productDto.getTransactNumberDay().getDayOfMonth()
            == productDto.getTransactNumberDay().plusMonths(1).getDayOfMonth()) {

          productDto.setAmount(productDto.getAmount() - transaction.getTransactionAmount());
          productDtoMono = filterSaveProduct(productDto);
        }
      } else {
        if (productDto.getAmount() != productDto.getCreditLimit()) {
          productDto.setAmount(productDto.getAmount() + transaction.getTransactionAmount());
          productDtoMono = filterSaveProduct(productDto);
        }
      }
    }
    return productDtoMono;
  }

  /**
   * Method that filters the storage of the product.
   *
   * @param productDto -> is object find.
   * @return a product condition.
   */
  public Mono<ProductDto> filterSaveProduct(ProductDto productDto) {
    Mono<ProductDto> productDtoMono = Mono.just(new ProductDto());
    if (productDto.getAccountType().equalsIgnoreCase(ConstantsCredit.CREDIT.name())) {
      productDtoMono = webClientProductHelper.updateProduct(productDto.getId(), productDto);
    }
    return productDtoMono;
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
    if (transaction.getTransactionType().equalsIgnoreCase(ConstantsCreditTransac.CREDIT_PAYMENT.name())) {
      optionRemove = findToProduct.flatMap(productDto -> {
        productDto.setAmount(productDto.getAmount() + transaction.getTransactionAmount());
        return webClientProductHelper.updateProduct(productDto.getId(), productDto)
          .flatMap(productDto1 -> Mono.just(true));
      });
    } else {
      optionRemove = findToProduct.flatMap(productDto -> {
        productDto.setAmount(productDto.getAmount() - transaction.getTransactionAmount());
        return webClientProductHelper.updateProduct(productDto.getId(), productDto)
          .flatMap(productDto1 -> Mono.just(true));
      });
    }

    return optionRemove;
  }

  /**
   * Method to condition the saving of the transaction Credits.
   *
   * @param transaction -> attribute object type transaction.
   * @return the condition for saving.
   */
  public Mono<ProductDto> filterCredit(TransactionDto transaction, Flux<Transaction> transactionFlux) {
    Mono<List<Transaction>> transactionMono = transactionFlux.collectList();
    return transactionMono.flatMap(transactionList -> isSave(transaction));
  }
}
