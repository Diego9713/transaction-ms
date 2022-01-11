package bootcamp.com.transactionms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("transaction")
public class Transaction {
    @Id
    private String id;
    @Field(name = "product_id")
    private String productId;
    @Field(name = "payment_method")
    private String paymentMethod;
    @Field(name = "transaction_type")
    private String transactionType;
    @Field(name = "transaction_amount")
    private double transactionAmount;
    @Field(name = "created_at")
    private Date createdAt;
    @Field(name = "created_by")
    private String createdBy;
    @Field(name = "update_at")
    private Date updateAt;
    @Field(name = "update_by")
    private String updateBy;
    @Field(name = "status")
    private String status;
}
