package com.obysoft.faithOS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FaithOsApplication {

	public static void main(String[] args) {
		SpringApplication.run(FaithOsApplication.class, args);
	}

}
