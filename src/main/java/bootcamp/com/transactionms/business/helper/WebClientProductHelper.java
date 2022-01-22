package bootcamp.com.transactionms.business.helper;

import bootcamp.com.transactionms.model.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class WebClientProductHelper {
  @Autowired
  private WebClient webClient;

  /**
   * Method to find product.
   *
   * @param id -> is the product identifier.
   * @return a object product.
   */
  public Mono<ProductDto> findProduct(String id) {
    return webClient.get()
      .uri("/api/v1/products/" + id)
      .retrieve()
      .bodyToMono(ProductDto.class);
  }

  /**
   * Method to find product by number account.
   *
   * @param account -> is the number product identifier.
   * @return a object product.
   */
  public Flux<ProductDto> findProductByAccount(String account) {
    return webClient.get()
      .uri("/api/v1/products/accountnumber/" + account)
      .retrieve()
      .bodyToFlux(ProductDto.class);
  }

  /**
   * Method to update product.
   *
   * @param id         -> is the product identifier.
   * @param productDto -> is the object to update.
   * @return a object product.
   */
  public Mono<ProductDto> updateProduct(String id, ProductDto productDto) {
    return webClient.put()
      .uri("/api/v1/products/" + id)
      .body(Mono.just(productDto), ProductDto.class)
      .retrieve()
      .bodyToMono(ProductDto.class);
  }
}
