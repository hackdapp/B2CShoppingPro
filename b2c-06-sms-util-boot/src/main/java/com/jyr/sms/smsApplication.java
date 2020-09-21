package com.jyr.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration
@SpringBootApplication
public class smsApplication {

	public static void main(String[] args) {
		SpringApplication.run(smsApplication.class, args);
	}

}
