package com.sibanarayan.code;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HiiCodersApplication {

	public static void main(String[] args) {
		SpringApplication.run(HiiCodersApplication.class, args);
	}

}
