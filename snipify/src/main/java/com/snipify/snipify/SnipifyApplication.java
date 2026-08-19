package com.snipify.snipify;

import com.snipify.snipify.service.UrlService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class SnipifyApplication {


	public static void main(String[] args) {
		SpringApplication.run(SnipifyApplication.class, args);

	}

}
