package com.api.bandlogs_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableTransactionManagement
public class BandlogsManagerApplication {
	public static void main(String[] args) {
		SpringApplication.run(BandlogsManagerApplication.class, args);
	}
}
