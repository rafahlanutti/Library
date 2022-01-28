package com.estudos.springboot.libraryapi.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

	@Bean
	public ModelMapper initConfiguration() {
		return new ModelMapper();
	}
}
