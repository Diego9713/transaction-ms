package bootcamp.com.transactionms.business.helper;

import bootcamp.com.transactionms.model.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WebClientProductHelper {
    @Autowired
    private WebClient webClient;

    public Mono<ProductDto> findProduct(String id){
        return webClient.get()
                .uri("/api/v1/products/" + id)
                .retrieve()
                .bodyToMono(ProductDto.class);
    }
    public Mono<ProductDto> updateProduct(String id,ProductDto productDto){
        return webClient.put()
                .uri("/api/v1/products/" + id)
                .body(Mono.just(productDto),ProductDto.class)
                .retrieve()
                .bodyToMono(ProductDto.class);
    }
}
