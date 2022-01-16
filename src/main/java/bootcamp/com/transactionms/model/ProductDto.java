package bootcamp.com.transactionms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductDto {
  private String id;
  private String accountType;
  private String accountNumber;
  private LocalDateTime createdAt;
  private String createdBy;
  private LocalDate updateAt;
  private String updateBy;
  private String currency;
  private double amount;
  private double maintenanceCommission;
  private LocalDateTime maintenanceCommissionDay;
  private double minimumAverageAmount = 0;
  private double averageDailyBalance = 0;
  private LocalDate averageDailyBalanceDay;
  private int maxTransactNumber;
  private LocalDateTime transactNumberDay;
  private double creditLimit;
  private String customer;
  private String status;
}
