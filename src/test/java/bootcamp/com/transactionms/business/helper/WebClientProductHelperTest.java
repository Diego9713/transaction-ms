package bootcamp.com.transactionms.business.helper;

import bootcamp.com.transactionms.model.ProductDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WebClientProductHelperTest {
  @Autowired
  private WebClientProductHelper webClientProductHelper;

  private static final ProductDto productDto = new ProductDto();
  private static final String idProduct = "61db64d731dec743727907f3";
  private static final String accountType = "SAVING";
  private static final String accountNumber = "d85c241a-2eb7-40da-938c-097f30d3756f";
  private static final String currency = "PEN";
  private static final double amount = 6300;
  private static final double maintenanceCommission = 0;
  private static final LocalDateTime maintenanceCommissionDay = null;
  private static final int maxTransactNumber = 10;
  private static final LocalDateTime transactNumberDay = null;
  private static final double creditLimit = 0;
  private static final String customer = "61db5ffd7610bd27a53b2b8b";
  private static final String statusProduct = "ACTIVE";
  private static final LocalDateTime createdAtProduct = LocalDateTime.now();
  private static final String createdByProduct = "pedro";
  private static final LocalDate updateAtProduct = LocalDate.now();
  private static final String updateByProduct = "pedro";
  private static final double minimumAverageAmount = 0;
  private static final double averageDailyBalance = 0;
  private static final LocalDate averageDailyBalanceDay = LocalDate.now();

  @BeforeAll
  static void setUp() {
    productDto.setId(idProduct);
    productDto.setAccountType(accountType);
    productDto.setAccountNumber(accountNumber);
    productDto.setCurrency(currency);
    productDto.setAmount(amount);
    productDto.setMaintenanceCommission(maintenanceCommission);
    productDto.setMaintenanceCommissionDay(maintenanceCommissionDay);
    productDto.setMaxTransactNumber(maxTransactNumber);
    productDto.setTransactNumberDay(transactNumberDay);
    productDto.setCreditLimit(creditLimit);
    productDto.setCustomer(customer);
    productDto.setStatus(statusProduct);
    productDto.setCreatedAt(createdAtProduct);
    productDto.setCreatedBy(createdByProduct);
    productDto.setUpdateAt(updateAtProduct);
    productDto.setUpdateBy(updateByProduct);
    productDto.setMinimumAverageAmount(minimumAverageAmount);
    productDto.setAverageDailyBalance(averageDailyBalance);
    productDto.setAverageDailyBalanceDay(averageDailyBalanceDay);
    productDto.getAccountType();
    productDto.getAccountNumber();
    productDto.getCurrency();
    productDto.getAmount();
    productDto.getMaintenanceCommission();
    productDto.getMaintenanceCommissionDay();
    productDto.getMaxTransactNumber();
    productDto.getTransactNumberDay();
    productDto.getCreditLimit();
    productDto.getCustomer();
    productDto.getStatus();
    productDto.getCreatedAt();
    productDto.getCreatedBy();
    productDto.getUpdateAt();
    productDto.getUpdateBy();
    productDto.getMinimumAverageAmount();
    productDto.getAverageDailyBalance();
    productDto.getAverageDailyBalanceDay();
  }


  @Test
  void findProduct() {
    Assertions.assertNotNull(webClientProductHelper.findProduct(idProduct));
  }

  @Test
  void updateProduct() {
    Assertions.assertNotNull(webClientProductHelper.updateProduct(idProduct, productDto));
  }
}