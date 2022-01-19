package bootcamp.com.transactionms.model;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@Document("transaction")
public class Transaction {
  @Id
  private String id;
  @Field(name = "product_id")
  private String productId;
  @Field(name = "from_product")
  private String fromProduct;
  @Field(name = "payment_method")
  private String paymentMethod;
  @Field(name = "transaction_type")
  private String transactionType;
  @Field(name = "transaction_amount")
  private double transactionAmount;
  @Field(name = "commission")
  private double commission = 0;
  @Field(name = "created_at")
  private String createdAt;
  @Field(name = "created_by")
  private String createdBy;
  @Field(name = "update_at")
  private LocalDate updateAt;
  @Field(name = "update_by")
  private String updateBy;
  @Field(name = "status")
  private String status;
}
