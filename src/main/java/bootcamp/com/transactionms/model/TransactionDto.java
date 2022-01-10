package bootcamp.com.transactionms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
    private String id;
    private String productId;
    private String paymentMethod;
    private String transactionType;
    private double transactionAmount;
    private Date createdAt;
    private String status;
}
