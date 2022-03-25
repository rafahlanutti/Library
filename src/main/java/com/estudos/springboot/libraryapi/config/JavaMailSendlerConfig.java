package com.estudos.springboot.libraryapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class JavaMailSendlerConfig {

	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

		javaMailSender.setProtocol("smtp");
		javaMailSender.setHost("smtp.mailtrap.io");
		javaMailSender.setPort(2525);
		javaMailSender.setUsername("1a2bcd58ed3297");
		javaMailSender.setPassword("fb77e0f5c5ea20");

		return javaMailSender;
	}
}
