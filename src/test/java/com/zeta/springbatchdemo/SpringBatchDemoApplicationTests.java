package com.zeta.springbatchdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest
class SpringBatchDemoApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	public void jobId() {
		DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		LocalDateTime localDateTime = LocalDateTime.now();
		String dateString = FOMATTER.format(localDateTime);

		System.out.println(dateString);
	}
}
