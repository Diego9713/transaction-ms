package bootcamp.com.transactionms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private String id;
    private String accountType;
    private String accountNumber;
    private Date createdAt;
    private String createdBy;
    private Date updateAt;
    private String updateBy;
    private String currency;
    private double amount;
    private double maintenanceCommission;
    private LocalDateTime maintenanceCommissionDay;
    private int maxTransactNumber;
    private LocalDateTime transactNumberDay;
    private double creditLimit;
    private String customer;
    private String status;
}
