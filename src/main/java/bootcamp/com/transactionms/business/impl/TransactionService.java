package bootcamp.com.transactionms.business.impl;

import bootcamp.com.transactionms.business.ITransactionService;
import bootcamp.com.transactionms.business.helper.FilterTransaction;
import bootcamp.com.transactionms.business.helper.FilterTransactionCredit;
import bootcamp.com.transactionms.business.helper.FilterTransactionDebit;
import bootcamp.com.transactionms.model.ProductDto;
import bootcamp.com.transactionms.model.Transaction;
import bootcamp.com.transactionms.model.TransactionDto;
import bootcamp.com.transactionms.repository.ITransactionRepository;
import bootcamp.com.transactionms.utils.AppUtils;
import bootcamp.com.transactionms.utils.ConstantsTransacStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service("TransactionService")
@Slf4j
public class TransactionService implements ITransactionService {

    @Autowired
    private ITransactionRepository transactionRepository;
    @Autowired
    private FilterTransactionDebit filterTransactionDebit;
    @Autowired
    private FilterTransactionCredit filterTransactionCredit;
    @Autowired
    private FilterTransaction filterTransaction;

    /**
     * Method to find all transactions.
     *
     * @return a list of transaction.
     */
    @Override
    @Transactional(readOnly = true)
    public Flux<TransactionDto> findAllTransaction() {
        log.info("FindAll >>>");
        return transactionRepository.findAll()
                .map(AppUtils::entityToTransactionDto);
    }

    /**
     * Method to search for a transaction by id.
     *
     * @param id -> identifier of transaction.
     * @return object of transaction.
     */
    @Override
    @Transactional(readOnly = true)
    public Mono<TransactionDto> findByIdTransaction(String id) {
        log.info("FindById >>>");
        return transactionRepository.findById(id)
                .map(AppUtils::entityToTransactionDto)
                .filter(transactionDto -> transactionDto.getStatus()
                        .equalsIgnoreCase(ConstantsTransacStatus.COMPLETE.name()));
    }

    /**
     * Method to search for a transaction by product.
     *
     * @param productId -> identifier of product.
     * @return object of transaction.
     */
    @Override
    @Transactional(readOnly = true)
    public Flux<TransactionDto> findTransactionByProduct(String productId) {
        log.info("Find transactionByProduct >>>");
        return transactionRepository.findByProductId(productId)
                .map(AppUtils::entityToTransactionDto)
                .filter(transactionDto -> transactionDto.getStatus()
                        .equalsIgnoreCase(ConstantsTransacStatus.COMPLETE.name()));
    }

    /**
     * Method to save a transaction type Debits.
     *
     * @param transaction -> attribute object type transaction.
     * @return the transaction saved.
     */

    @Override
    @Transactional
    public Mono<TransactionDto> createTransactionDebit(Transaction transaction) {
        log.info("Save Transaction Debit >>>");
        Mono<ProductDto> filterProduct = filterDebit(transaction);
        return filterProduct.flatMap(productDto -> productDto.getId() != null ?
                filterTransaction.filterTransactionCreate(transaction)
                        .flatMap(transactionRepository::insert)
                        .map(AppUtils::entityToTransactionDto) : Mono.just(new TransactionDto()));
    }

    /**
     * Method to update a transaction type Debits.
     *
     * @param transaction -> attribute object type transaction.
     * @param id          -> identifier of transaction.
     * @return the transaction update.
     */
    @Override
    @Transactional
    public Mono<TransactionDto> updateTransactionDebit(Transaction transaction, String id) {
        log.info("Update Transaction Debit >>>");
        Mono<Transaction> findTransaction = transactionRepository.findById(id);
        Mono<ProductDto> filterProduct = findTransaction.switchIfEmpty(Mono.empty())
                .flatMap(transaction1 -> {
                    transaction.setId(id);
                    if (transaction1.getTransactionAmount() > transaction.getTransactionAmount())
                        transaction.setTransactionAmount(transaction1.getTransactionAmount() - transaction.getTransactionAmount());
                    else
                        transaction.setTransactionAmount(transaction.getTransactionAmount() - transaction1.getTransactionAmount());
                    return filterDebit(transaction);
                });
        return filterProduct.flatMap(productDto -> productDto.getId() != null ?
                filterTransaction.filterTransactionUpdate(findTransaction)
                        .flatMap(transactionModel -> {
                            transactionModel.setId(id);
                            return transactionRepository.save(transactionModel);
                        })
                        .map(AppUtils::entityToTransactionDto) : Mono.just(new TransactionDto()));
    }

    /**
     * Method to remove a transaction type Debits.
     *
     * @param id -> identifier of transaction.
     * @return the transaction remove.
     */
    @Override
    @Transactional
    public Mono<TransactionDto> removeTransactionDebit(String id) {
        log.info("Remove Transaction Debit >>>");
        return transactionRepository.findById(id).switchIfEmpty(Mono.empty())
                .filter(transaction -> transaction.getStatus()
                        .equalsIgnoreCase(ConstantsTransacStatus.COMPLETE.name()))
                .flatMap(transaction -> filterTransactionDebit.filterRemoveProduct(transaction)
                        .flatMap(optionRemove -> optionRemove ?
                                filterTransaction.filterTransactionDelete(transaction)
                                        .flatMap(transactionRepository::save)
                                        .map(AppUtils::entityToTransactionDto) : Mono.just(new TransactionDto())));
    }

    /**
     * Method to save a transaction type Credits.
     *
     * @param transaction -> attribute object type transaction.
     * @return the transaction saved.
     */
    @Override
    @Transactional
    public Mono<TransactionDto> createTransactionCredit(Transaction transaction) {
        log.info("Save Transaction Credit >>>");
        Mono<ProductDto> filterProduct = filterCredit(transaction);
        return filterProduct.flatMap(productDto -> productDto.getId() != null ?
                filterTransaction.filterTransactionCreate(transaction)
                        .flatMap(transactionRepository::insert)
                        .map(AppUtils::entityToTransactionDto) : Mono.just(new TransactionDto()));
    }

    /**
     * Method to update a transaction type Credits.
     *
     * @param transaction -> attribute object type transaction.
     * @param id          -> identifier of transaction.
     * @return the transaction update.
     */
    @Override
    @Transactional
    public Mono<TransactionDto> updateTransactionCredit(Transaction transaction, String id) {
        log.info("Update Transaction Credit >>>");
        Mono<Transaction> findTransaction = transactionRepository.findById(id);
        Mono<ProductDto> filterProduct = findTransaction.switchIfEmpty(Mono.empty())
                .flatMap(transaction1 -> filterCredit(transaction));
        return filterProduct.flatMap(productDto -> productDto.getId() != null ?
                filterTransaction.filterTransactionUpdate(findTransaction)
                        .flatMap(transactionModel -> {
                            transactionModel.setId(id);

                            if (transactionModel.getTransactionAmount() > transaction.getTransactionAmount())
                                transactionModel.setTransactionAmount(transactionModel.getTransactionAmount() - transaction.getTransactionAmount());
                            else
                                transactionModel.setTransactionAmount(transaction.getTransactionAmount() - transactionModel.getTransactionAmount());
                            return transactionRepository.save(transactionModel);
                        })
                        .map(AppUtils::entityToTransactionDto) : Mono.just(new TransactionDto()));
    }

    /**
     * Method to remove a transaction type Credits.
     *
     * @param id -> identifier of transaction.
     * @return the transaction remove.
     */
    @Override
    @Transactional
    public Mono<TransactionDto> removeTransactionCredit(String id) {
        log.info("Remove Transaction Credit >>>");
        return transactionRepository.findById(id)
                .switchIfEmpty(Mono.empty())
                .filter(transaction -> transaction.getStatus()
                        .equalsIgnoreCase(ConstantsTransacStatus.COMPLETE.name()))
                .flatMap(transaction -> filterTransactionCredit.filterRemoveProduct(transaction)
                        .flatMap(optionRemove -> optionRemove ?
                                filterTransaction.filterTransactionDelete(transaction)
                                        .flatMap(transactionRepository::save)
                                        .map(AppUtils::entityToTransactionDto) : Mono.just(new TransactionDto())));
    }

    /**
     * Method to condition the saving of the transaction Debits
     *
     * @param transaction -> attribute object type transaction.
     * @return the condition for saving.
     */
    public Mono<ProductDto> filterDebit(Transaction transaction) {
        log.info(" Filter Transaction Debit >>>");
        Mono<List<Transaction>> transactionFlux = transactionRepository.findByProductId(transaction.getProductId()).collectList();
        return transactionFlux.flatMap(transactionList -> filterTransactionDebit.isSave(transaction, transactionList));
    }

    /**
     * Method to condition the saving of the transaction Credits
     *
     * @param transaction -> attribute object type transaction.
     * @return the condition for saving.
     */
    public Mono<ProductDto> filterCredit(Transaction transaction) {
        log.info(" Filter Transaction Credit >>>");
        Mono<List<Transaction>> transactionFlux = transactionRepository.findByProductId(transaction.getProductId()).collectList();
        return transactionFlux.flatMap(transactionList -> filterTransactionCredit.isSave(transaction));

    }

}
