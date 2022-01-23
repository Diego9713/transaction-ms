package bootcamp.com.transactionms;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TransactionMsApplicationTests {

	@Test
	void contextLoads() {
		Assertions.assertNotNull(TransactionMsApplication.class);
	}

}
