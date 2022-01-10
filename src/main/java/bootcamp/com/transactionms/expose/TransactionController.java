package bootcamp.com.transactionms.expose;

import bootcamp.com.transactionms.business.ITransactionService;
import bootcamp.com.transactionms.model.Transaction;
import bootcamp.com.transactionms.model.TransactionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/v1/transactions")
public class TransactionController {
    @Autowired
    @Qualifier("TransactionService")
    private ITransactionService transactionService;

    @GetMapping("")
    public Flux<TransactionDto> findAllTransaction(){
        return transactionService.findAllTransaction();
    }

    @GetMapping("/product/{productId}")
    public Flux<TransactionDto> findTransactionByProduct(@PathVariable("productId") String productId){
        return transactionService.findTransactionByProduct(productId);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<TransactionDto>> findOneTransaction(@PathVariable String id){
        return transactionService.findByIdTransaction(id)
                .flatMap(p->Mono.just(ResponseEntity.ok().body(p)))
                .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));

    }

    @PostMapping("/debits")
    public Mono<ResponseEntity<TransactionDto>> saveTransactionDebit(@RequestBody Transaction transaction){
        return transactionService.createTransactionDebit(transaction)
                .flatMap(p->Mono.just(ResponseEntity.ok().body(p)))
                .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
    }

    @PutMapping("/debits/{id}")
    public Mono<ResponseEntity<TransactionDto>> updateTransactionDebit(@PathVariable String id,@RequestBody Transaction transaction){
        return transactionService.updateTransactionDebit(transaction,id)
                .flatMap(p->Mono.just(ResponseEntity.ok().body(p)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/debits/{id}")
    public Mono<ResponseEntity<TransactionDto>> removeTransactionDebit(@PathVariable String id){
        return transactionService.removeTransactionDebit(id)
                .flatMap(p->Mono.just(ResponseEntity.ok().body(p)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping("/credits")
    public Mono<ResponseEntity<TransactionDto>> saveTransactionCredit(@RequestBody Transaction transaction){
        return transactionService.createTransactionCredit(transaction)
                .flatMap(p->Mono.just(ResponseEntity.ok().body(p)))
                .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
    }

    @PutMapping("/credits/{id}")
    public Mono<ResponseEntity<TransactionDto>> updateTransactionCredit(@PathVariable String id,@RequestBody Transaction transaction){
        return transactionService.updateTransactionCredit(transaction,id)
                .flatMap(p->Mono.just(ResponseEntity.ok().body(p)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/credits/{id}")
    public Mono<ResponseEntity<TransactionDto>> removeTransactionCredit(@PathVariable("id") String id){
        return transactionService.removeTransactionCredit(id)
                .flatMap(p->Mono.just(ResponseEntity.ok().body(p)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
}
