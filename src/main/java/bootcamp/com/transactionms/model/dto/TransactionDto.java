package bootcamp.com.transactionms.model.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TransactionDto {
  private String id;
  private String productId;
  private String fromProduct;
  private String paymentMethod;
  private String transactionType;
  private double transactionAmount;
  private double commission = 0;
  private String createdAt;
  private String createdBy;
  private LocalDate updateAt;
  private String updateBy;
  private String status;
}
