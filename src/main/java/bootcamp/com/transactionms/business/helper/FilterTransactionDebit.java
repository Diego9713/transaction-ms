package bootcamp.com.transactionms.business.helper;

import bootcamp.com.transactionms.model.ProductDto;
import bootcamp.com.transactionms.model.Transaction;
import bootcamp.com.transactionms.utils.ConstantsDebit;
import bootcamp.com.transactionms.utils.ConstantsDebitTransac;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class FilterTransactionDebit {
    @Autowired
    private WebClientProductHelper webClientProductHelper;

    /**
     * Method to configure the saving of the transaction
     * @param transaction is object sending
     * @param transactionFlux is the number of transactions carried out
     * @return a object product saving
     */
    public Mono<ProductDto> isSave(Transaction transaction, List<Transaction> transactionFlux) {
        Mono<ProductDto> findToProduct = webClientProductHelper.findProduct(transaction.getProductId());
        return findToProduct.filter(e -> Arrays.stream(ConstantsDebit.values()).anyMatch(m -> m.toString().equalsIgnoreCase(e.getAccountType())))
                .switchIfEmpty(Mono.just(new ProductDto()))
                .flatMap(productDto -> isTypeTransfer(transaction, productDto, transactionFlux));
    }

    public Mono<ProductDto> isTypeTransfer(Transaction transaction, ProductDto productDto, List<Transaction> transactionFlux) {
        Mono<ProductDto> productDtoMono = Mono.just(new ProductDto());

        if (Arrays.stream(ConstantsDebitTransac.values()).anyMatch(m -> m.toString().equalsIgnoreCase(transaction.getTransactionType()))) {
            if (transaction.getTransactionType().equalsIgnoreCase(ConstantsDebitTransac.DEPOSIT.name())) {

                productDto.setAmount(productDto.getAmount() + transaction.getTransactionAmount());

                productDtoMono = filterSaveProduct(productDto, transactionFlux, transaction);
            } else {
                if (productDto.getAmount() > 0 && productDto.getAmount() > transaction.getTransactionAmount()) {

                    productDto.setAmount(productDto.getAmount() - transaction.getTransactionAmount());

                    productDtoMono = filterSaveProduct(productDto, transactionFlux, transaction);
                }
            }
        }
        return productDtoMono;
    }

    public Mono<ProductDto> filterSaveProduct(ProductDto productDto, List<Transaction> transactionFlux, Transaction transaction) {
        Mono<ProductDto> productDtoMono = Mono.just(new ProductDto());
        if (productDto.getAccountType().equalsIgnoreCase(ConstantsDebit.SAVING.name())) {
            if (transactionFlux.size() != productDto.getMaxTransactNumber() || transaction.getId() != null) {
                productDtoMono = webClientProductHelper.updateProduct(productDto.getId(), productDto);
            }
        }
        if (productDto.getAccountType().equalsIgnoreCase(ConstantsDebit.CURRENT.name())) {
            if (LocalDateTime.now().isEqual(productDto.getMaintenanceCommissionDay()) || transaction.getId() != null) {
                if (transaction.getId() == null) {
                    productDto.setMaintenanceCommissionDay(LocalDateTime.now().plusMonths(1));
                }
                productDto.setAmount(productDto.getAmount() - productDto.getMaintenanceCommission());
            }
            productDtoMono = webClientProductHelper.updateProduct(productDto.getId(), productDto);
        }
        if (productDto.getAccountType().equalsIgnoreCase(ConstantsDebit.FIXED_TERM.name())) {
            if (transactionFlux.size() < productDto.getMaxTransactNumber() && LocalDateTime.now().isEqual(productDto.getTransactNumberDay()) || transaction.getId() != null) {
                if (transaction.getId() == null) {
                    productDto.setTransactNumberDay(LocalDateTime.now().plusDays(10));
                }
                productDtoMono = webClientProductHelper.updateProduct(productDto.getId(), productDto);
            }
        }

        return productDtoMono;
    }

    public Mono<Boolean> filterRemoveProduct(Transaction transaction) {
        Mono<Boolean> optionRemove;
        Mono<ProductDto> findToProduct = webClientProductHelper.findProduct(transaction.getProductId());
        if (transaction.getTransactionType().equalsIgnoreCase(ConstantsDebitTransac.DEPOSIT.name())) {
            optionRemove = findToProduct.flatMap(productDto -> {
                productDto.setAmount(productDto.getAmount() - transaction.getTransactionAmount());
                return webClientProductHelper.updateProduct(productDto.getId(), productDto).flatMap(productDto1 -> Mono.just(true));
            });
        } else {
            optionRemove = findToProduct.flatMap(productDto -> {
                productDto.setAmount(productDto.getAmount() + transaction.getTransactionAmount());
                return webClientProductHelper.updateProduct(productDto.getId(), productDto).flatMap(productDto1 -> Mono.just(true));
            });
        }

        return optionRemove;
    }

}
