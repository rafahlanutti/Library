package com.estudos.springboot.libraryapi.controller;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.estudos.springboot.libraryapi.dto.BookDTO;
import com.estudos.springboot.libraryapi.entity.Book;
import com.estudos.springboot.libraryapi.exception.ObjectNotFoundException;
import com.estudos.springboot.libraryapi.service.BookService;

@RestController
@RequestMapping("/api/books")
public class BookController {

	private BookService service;
	private ModelMapper modelMapper;

	@Autowired
	public BookController(BookService service, ModelMapper modelMapper) {
		super();
		this.service = service;
		this.modelMapper = modelMapper;
	}

	@GetMapping("{id}")
	public BookDTO get(@PathVariable Long id) {
		var finded = service.getById(id);

		if (finded.isPresent()) {
			return modelMapper.map(finded.get(), BookDTO.class);
		}

		throw new ObjectNotFoundException(HttpStatus.NOT_FOUND, "Objeto não encontrado");
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookDTO create(@Valid @RequestBody BookDTO dto) {

		Book entity = modelMapper.map(dto, Book.class);
		entity = service.save(entity);
		return modelMapper.map(entity, BookDTO.class);
	}

}
