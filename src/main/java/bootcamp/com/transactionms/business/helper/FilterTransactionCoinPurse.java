package bootcamp.com.transactionms.business.helper;

import bootcamp.com.transactionms.config.KafkaMessageProducer;
import bootcamp.com.transactionms.model.dto.CoinPurseDto;
import bootcamp.com.transactionms.model.dto.KafkaMessageDto;
import bootcamp.com.transactionms.model.dto.ProductDto;
import bootcamp.com.transactionms.model.dto.TransactionDto;
import bootcamp.com.transactionms.utils.ConstantsCoinPurse;
import bootcamp.com.transactionms.utils.ConstantsPayMethod;
import bootcamp.com.transactionms.utils.ConstantsTransacStatus;
import com.google.gson.Gson;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class FilterTransactionCoinPurse {
  @Autowired
  private WebClientCoinPurseHelper webClientCoinPurseHelper;
  @Autowired
  private WebClientProductHelper webClientProductHelper;
  @Autowired
  private KafkaMessageProducer kafkaMessageProducer;

  /**
   * Method to search coin purse for from id.
   *
   * @param fromCoinPurse -> identify unique to from.
   * @return object type coin purse.
   */
  public Mono<CoinPurseDto> searchFromCoinPurse(String fromCoinPurse) {
    return webClientCoinPurseHelper.findCoinPurse(fromCoinPurse)
      .switchIfEmpty(Mono.empty());
  }

  /**
   * Method to search coin purse for To id.
   *
   * @param toCoinPurse -> identify unique to To.
   * @return object type coin purse.
   */
  public Mono<CoinPurseDto> searchToCoinPurse(String toCoinPurse) {
    return webClientCoinPurseHelper.findCoinPurse(toCoinPurse)
      .switchIfEmpty(Mono.empty());
  }

  /**
   * Method to filter coin purse for payment method.
   *
   * @param coinPurseDto   -> object find for coin purse type.
   * @param transactionDto -> object transaction for create or update.
   * @return object check boolean.
   */
  public Mono<Boolean> filterCoinPurse(CoinPurseDto coinPurseDto, TransactionDto transactionDto) {
    Mono<Boolean> bool = Mono.just(false);
    if (transactionDto.getPaymentMethod().equalsIgnoreCase(ConstantsPayMethod.COIN_PURSE.name())) {
      if (coinPurseDto.getCoinPurseType().equalsIgnoreCase(ConstantsCoinPurse.COIN_PURSE_DNI.name())) {
        bool = coinPurseTypeDni(coinPurseDto, transactionDto);
      } else {
        bool = coinPurseTypeCard(coinPurseDto, transactionDto);
      }
    }
    return bool;
  }

  /**
   * Method to filter coin purse for DNI.
   *
   * @param coinPurseDto   -> object find for coin purse type.
   * @param transactionDto -> object transaction for create or update.
   * @return object check boolean.
   */
  public Mono<Boolean> coinPurseTypeDni(CoinPurseDto coinPurseDto, TransactionDto transactionDto) {
    Mono<Boolean> bool = null;
    if (transactionDto.getProductId().equalsIgnoreCase(coinPurseDto.getId())) {
      if (transactionDto.getId() != null) {
        coinPurseDto.setAmount(coinPurseDto.getAmount() - transactionDto.getTransactionAmount());
      } else {
        coinPurseDto.setAmount(coinPurseDto.getAmount() + transactionDto.getTransactionAmount());
      }
      Mono<CoinPurseDto> coinPurse = webClientCoinPurseHelper.updateCoinPurse(coinPurseDto.getId(), coinPurseDto);
      bool = coinPurse.map(c -> c.getId() != null ? Boolean.TRUE : Boolean.FALSE);
    } else {
      double amount = 0;
      if (transactionDto.getId() != null) {
        amount = coinPurseDto.getAmount() + transactionDto.getTransactionAmount();
      } else {
        amount = coinPurseDto.getAmount() - transactionDto.getTransactionAmount();
      }
      coinPurseDto.setAmount(amount);
      Mono<CoinPurseDto> coinPurse = webClientCoinPurseHelper.updateCoinPurse(coinPurseDto.getId(), coinPurseDto);
      bool = coinPurse.map(c -> c.getId() != null ? Boolean.TRUE : Boolean.FALSE);

    }
    return bool;
  }

  /**
   * Method to filter coin purse for Card.
   *
   * @param coinPurseDto   -> object find for coin purse type.
   * @param transactionDto -> object transaction for create or update.
   * @return object check boolean.
   */
  public Mono<Boolean> coinPurseTypeCard(CoinPurseDto coinPurseDto, TransactionDto transactionDto) {
    Mono<Boolean> boolProduct = null;
    Mono<Boolean> bool = null;
    Flux<ProductDto> productDtoFlux = webClientProductHelper.findProductByAccount(coinPurseDto.getAccountNumber());
    Mono<List<ProductDto>> productDtoList = productDtoFlux.collectList();
    boolProduct = productDtoList.flatMap(productDtos -> saveProduct(coinPurseDto, transactionDto, productDtos));
    bool = boolProduct.filter(Boolean.TRUE::equals)
      .flatMap(b -> webClientCoinPurseHelper.updateCoinPurse(coinPurseDto.getId(), coinPurseDto))
      .map(c -> c.getId() != null ? Boolean.TRUE : Boolean.FALSE);

    return bool;
  }

  /**
   * Method to save product and coin purse.
   *
   * @param coinPurseDto   -> object find for coin purse type.
   * @param transactionDto -> object transaction for create or update.
   * @param productDtoList -> list object product to search account principal.
   * @return object check boolean.
   */
  public Mono<Boolean> saveProduct(CoinPurseDto coinPurseDto, TransactionDto transactionDto, List<ProductDto> productDtoList) {
    Mono<Boolean> bool = Mono.just(false);
    ProductDto newProduct;
    for (ProductDto productDto : productDtoList) {
      if (productDto.getSubAccountNumber().equalsIgnoreCase(coinPurseDto.getAccountNumber())) {
        double amount = productDto.getAmount();
        newProduct = filterProduct(productDto, transactionDto, coinPurseDto);
        if (newProduct.getAmount() != amount) {
          Mono<ProductDto> product = webClientProductHelper.updateProduct(newProduct.getId(), newProduct);
          bool = product.map(p -> p.getId() != null ? Boolean.TRUE : Boolean.FALSE);
        }
        break;
      }
    }
    return bool;
  }

  /**
   * Method to filter product for create or update amount.
   *
   * @param productDtoMono -> object of account principal.
   * @param transactionDto -> object transaction for create or update.
   * @param coinPurseDto   -> object find for coin purse type.
   * @return object type product.
   */
  public ProductDto filterProduct(ProductDto productDtoMono, TransactionDto transactionDto, CoinPurseDto coinPurseDto) {
    if (transactionDto.getProductId().equalsIgnoreCase(coinPurseDto.getId())) {
      if (transactionDto.getId() != null) {
        productDtoMono.setAmount(productDtoMono.getAmount() - transactionDto.getTransactionAmount());
        coinPurseDto.setAmount(coinPurseDto.getAmount() - transactionDto.getTransactionAmount());
      }
      productDtoMono.setAmount(productDtoMono.getAmount() + transactionDto.getTransactionAmount());
      coinPurseDto.setAmount(coinPurseDto.getAmount() + transactionDto.getTransactionAmount());
    } else {
      double amount = 0;
      if (transactionDto.getId() != null) {
        amount = productDtoMono.getAmount() + transactionDto.getTransactionAmount();
      } else {
        amount = productDtoMono.getAmount() - transactionDto.getTransactionAmount();
      }
      if (productDtoMono.getAmount() > 0 && amount >= 0) {
        productDtoMono.setAmount(amount);
        coinPurseDto.setAmount(amount);
      }
    }

    return productDtoMono;
  }

  /**
   * Method to filter new amount transaction.
   *
   * @param findtransactionDto -> object find by id.
   * @param transactionDto     -> object transaction for create or update.
   * @return object transaction.
   */
  public Mono<TransactionDto> filterUpdateTransaction(TransactionDto findtransactionDto, TransactionDto transactionDto) {
    findtransactionDto.setTransactionAmount(findtransactionDto.getTransactionAmount() - transactionDto.getTransactionAmount());
    return Mono.just(findtransactionDto);
  }

  /**
   * Method to set attribute to transaction.
   *
   * @param transactionDto -> object transaction for create or update.
   * @param amount         -> amount before change.
   * @return object transaction type.
   */
  public Mono<TransactionDto> setAttributeTransaction(TransactionDto transactionDto, double amount) {
    transactionDto.setTransactionAmount(amount);
    return Mono.just(transactionDto);
  }

  /**
   * Method to remove transaction.
   *
   * @param transactionDto -> object find by id transaction type.
   * @return object check boolean.
   */
  public Mono<Boolean> removeTransaction(TransactionDto transactionDto) {
    Mono<Boolean> bool = null;
    Mono<CoinPurseDto> coinPurseTo = searchToCoinPurse(transactionDto.getProductId());
    Mono<CoinPurseDto> coinPurseFrom = searchFromCoinPurse(transactionDto.getFromProduct());
    bool = coinPurseFrom.flatMap(coinFrom -> removeFrom(transactionDto, coinFrom))
      .filter(Boolean.TRUE::equals)
      .flatMap(b -> coinPurseTo.flatMap(coinTo -> removeTo(transactionDto, coinTo)));

    return bool;
  }

  /**
   * Method to change amount to transaction from.
   *
   * @param transactionDto -> object find by id transaction type.
   * @param coinPurseDto   ->  object find by id coin purse type.
   * @return object check boolean.
   */
  public Mono<Boolean> removeFrom(TransactionDto transactionDto, CoinPurseDto coinPurseDto) {
    Mono<Boolean> bool = null;
    coinPurseDto.setAmount(coinPurseDto.getAmount() + transactionDto.getTransactionAmount());
    bool = removeCoinPurse(transactionDto, coinPurseDto);
    return bool;
  }

  /**
   * Method to change amount to transaction To.
   *
   * @param transactionDto -> object find by id transaction type.
   * @param coinPurseDto   ->  object find by id coin purse type.
   * @return object check boolean.
   */
  public Mono<Boolean> removeTo(TransactionDto transactionDto, CoinPurseDto coinPurseDto) {
    Mono<Boolean> bool = null;
    coinPurseDto.setAmount(coinPurseDto.getAmount() - transactionDto.getTransactionAmount());
    bool = removeCoinPurse(transactionDto, coinPurseDto);
    return bool;
  }

  /**
   * Method to filter remove coin purse.
   *
   * @param transactionDto -> object find by id transaction type.
   * @param coinPurseDto   -> object find by id coin purse type.
   * @return object check boolean.
   */
  public Mono<Boolean> removeCoinPurse(TransactionDto transactionDto, CoinPurseDto coinPurseDto) {
    Mono<Boolean> bool = null;
    if (coinPurseDto.getCoinPurseType().equalsIgnoreCase(ConstantsCoinPurse.COIN_PURSE_DNI.name())) {
      Mono<CoinPurseDto> coinPurse = webClientCoinPurseHelper.updateCoinPurse(coinPurseDto.getId(), coinPurseDto);
      bool = coinPurse.map(p -> p.getId() != null ? Boolean.TRUE : Boolean.FALSE);
    } else {
      Flux<ProductDto> productDtoFlux = webClientProductHelper.findProductByAccount(coinPurseDto.getAccountNumber());
      Mono<List<ProductDto>> productDtoList = productDtoFlux.collectList();

      bool = productDtoList.flatMap(productDtos -> saveProductRemove(coinPurseDto, transactionDto, productDtos))
        .filter(Boolean.TRUE::equals)
        .flatMap(b -> webClientCoinPurseHelper.updateCoinPurse(coinPurseDto.getId(), coinPurseDto))
        .map(c -> c.getId() != null ? Boolean.TRUE : Boolean.FALSE);
    }
    return bool;
  }

  /**
   * Method to save remove product.
   *
   * @param coinPurseDto   -> object find by id coin purse type.
   * @param transactionDto -> object find by id transaction type.
   * @param productDtoList -> list object to find account principal.
   * @return object check boolean.
   */
  public Mono<Boolean> saveProductRemove(CoinPurseDto coinPurseDto, TransactionDto transactionDto, List<ProductDto> productDtoList) {
    Mono<Boolean> bool = Mono.just(false);
    for (ProductDto productDto : productDtoList) {
      if (productDto.getSubAccountNumber().equalsIgnoreCase(coinPurseDto.getAccountNumber())) {
        if (coinPurseDto.getId().equalsIgnoreCase(transactionDto.getFromProduct())) {
          productDto.setAmount(productDto.getAmount() + transactionDto.getTransactionAmount());
        } else {
          productDto.setAmount(productDto.getAmount() - transactionDto.getTransactionAmount());
        }
        Mono<ProductDto> product = webClientProductHelper.updateProduct(productDto.getId(), productDto);
        bool = product.map(p -> p.getId() != null ? Boolean.TRUE : Boolean.FALSE);
        break;
      }
    }
    return bool;
  }

  /**
   * Method to change status transaction.
   *
   * @param transactionDto -> object find by id transaction type.
   * @return object transaction type.
   */
  public Mono<TransactionDto> changeStatusTransaction(TransactionDto transactionDto) {
    transactionDto.setStatus(ConstantsTransacStatus.REMOVE.name());
    return Mono.just(transactionDto);
  }

  /**
   * Method to create message and sent for kafka.
   *
   * @param transactionDto -> object created successfully.
   * @return message create.
   */
  public String generateMessage(TransactionDto transactionDto) {
    KafkaMessageDto kafkaMessageDto = new KafkaMessageDto();
    kafkaMessageDto.setAccount(transactionDto.getFromProduct());
    kafkaMessageDto.setMessage("Sent you to amount of ");
    kafkaMessageDto.setAmount(transactionDto.getTransactionAmount());
    String gson = new Gson().toJson(kafkaMessageDto);
    kafkaMessageProducer.sendMessage(gson);
    return gson;
  }
}
