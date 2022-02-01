package bootcamp.com.transactionms.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TransactionDtoBootCoins {
  private String id;
  private String productId;
  private String fromProduct;
  private String paymentMethod;
  private String transactionType;
  private double transactionAmount;
  private String status;
}
