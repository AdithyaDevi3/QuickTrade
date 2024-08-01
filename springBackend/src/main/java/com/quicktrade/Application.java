package com.quicktrade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories("com.quicktrade.repository")
public class Application {

	public static void main(String[] args) {
		try {
			SpringApplication.run(Application.class, args);
		}
		catch(Exception e){
			System.out.println(e);
		}
	}

}
