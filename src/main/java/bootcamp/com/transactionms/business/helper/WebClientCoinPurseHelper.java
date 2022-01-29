package bootcamp.com.transactionms.business.helper;

import bootcamp.com.transactionms.model.dto.CoinPurseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WebClientCoinPurseHelper {
  @Autowired
  private WebClient webClient;

  /**
   * Method to find coin purse.
   *
   * @param id -> is the coin purse identifier.
   * @return a coin purse.
   */
  public Mono<CoinPurseDto> findCoinPurse(String id) {
    return webClient.get()
      .uri("/api/v1/yankis/" + id)
      .retrieve()
      .bodyToMono(CoinPurseDto.class);
  }

  /**
   * Method to update coin purse.
   *
   * @param id         -> is the product identifier.
   * @param coinPurseDto -> is the object to update.
   * @return a object coin purse.
   */
  public Mono<CoinPurseDto> updateCoinPurse(String id, CoinPurseDto coinPurseDto) {
    return webClient.put()
      .uri("/api/v1/yankis/" + id)
      .body(Mono.just(coinPurseDto), CoinPurseDto.class)
      .retrieve()
      .bodyToMono(CoinPurseDto.class);
  }
}
