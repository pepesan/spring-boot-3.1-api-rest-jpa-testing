package com.inetum.demo.main;

import com.inetum.demo.DemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void mainMethodExecutes() {
		DemoApplication.main(new String[0]);
	}

}
