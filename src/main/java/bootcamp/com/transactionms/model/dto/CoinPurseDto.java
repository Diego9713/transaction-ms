package bootcamp.com.transactionms.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CoinPurseDto {

  private String id;
  private String documentType;
  private String documentNumber;
  private String coinPurseType;
  private String firstName;
  private String lastName;
  private String phoneNumber;
  private String phoneImei;
  private String email;
  private String accountNumber;
  private double amount;
  private String status;
}
