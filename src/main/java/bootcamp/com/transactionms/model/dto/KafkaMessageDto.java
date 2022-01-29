package bootcamp.com.transactionms.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KafkaMessageDto {
  private String account;
  private String message;
  private double amount;
}
