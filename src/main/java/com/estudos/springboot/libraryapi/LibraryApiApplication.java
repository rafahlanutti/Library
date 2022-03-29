package com.estudos.springboot.libraryapi;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.estudos.springboot.libraryapi.service.EmailService;

@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties
@ComponentScan
@SpringBootApplication
@ComponentScan(basePackages = "com.estudos.springboot.libraryapi")
@EnableScheduling
public class LibraryApiApplication {

	@Autowired
	private EmailService emailService;

	public static void main(String[] args) {
		SpringApplication.run(LibraryApiApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner() {

		return args -> {

			List<String> emails = Arrays.asList("3a96d1ba82-fe918b@inbox.mailtrap.io");
			emailService.sendMails("Testando envio de emails", emails);
			System.out.println("Emails enviados");
		};
	}

}
