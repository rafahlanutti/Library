package com.estudos.springboot.libraryapi.messages;

import java.util.Locale;

import org.springframework.context.MessageSource;

import com.estudos.springboot.libraryapi.config.MessageSourceConfig;

public enum Messages {
	NOT_FOUND("library-api.not-found-message"),
	ISNB_NOT_ALLOW("library-api.isbn-not-allow");

	public final String name;

	private final MessageSource messageSource = new MessageSourceConfig().messageSource();

	Messages(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return messageSource.getMessage(this.name, new Object[0], new Locale("el"));

	}

}