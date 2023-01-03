package com.devsancabo.www.publicationsrw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class PublicationsRwApplication {

	public static void main(String[] args) {
		SpringApplication.run(PublicationsRwApplication.class, args);
	}

}
