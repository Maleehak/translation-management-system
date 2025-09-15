package com.tms.tms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.tms.tms")
public class TranslationManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TranslationManagementSystemApplication.class, args);
	}

}
