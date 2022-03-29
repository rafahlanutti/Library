package com.estudos.springboot.libraryapi.controller;

import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.estudos.springboot.libraryapi.dto.BookDTO;
import com.estudos.springboot.libraryapi.dto.LoanDTO;
import com.estudos.springboot.libraryapi.entity.Book;
import com.estudos.springboot.libraryapi.entity.Loan;
import com.estudos.springboot.libraryapi.service.BookService;
import com.estudos.springboot.libraryapi.service.LoanService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

	private final BookService service;
	private final LoanService loanService;
	private final ModelMapper modelMapper;

	@GetMapping("/{id}")
	public BookDTO get(@PathVariable Long id) {
		log.info("GET LIVROS");
		return modelMapper.map(service.getById(id), BookDTO.class);

	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookDTO create(@Valid @RequestBody BookDTO dto) {

		Book entity = modelMapper.map(dto, Book.class);
		entity = service.save(entity);
		return modelMapper.map(entity, BookDTO.class);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.info("DELETE LIVROS");
		service.delete(id);

		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<BookDTO> update(@PathVariable Long id, @RequestBody BookDTO dto) {
		log.info("PUT DE LIVRO");
		var book = modelMapper.map(dto, Book.class);
		return ResponseEntity.ok(modelMapper.map(service.update(book), BookDTO.class));

	}

	@GetMapping
	public Page<BookDTO> find(BookDTO dto, Pageable pageRequest) {

		log.info("GET DE LIVROS");
		var filter = modelMapper.map(pageRequest, Book.class);
		Page<Book> result = service.find(filter, pageRequest);
		var list = result.getContent().stream().map(entity -> modelMapper.map(entity, BookDTO.class))
				.collect(Collectors.toList());

		return new PageImpl<>(list, pageRequest, result.getTotalElements());
	}

	@GetMapping("{id}/loans")
	public Page<LoanDTO> loansByBook(@PathVariable Long id, Pageable pageable) {

		Book entity = service.getById(id);
		Page<Loan> loans = loanService.getLoansByBook(entity, pageable);
		var loansDTO = loans.getContent().stream().map(loan -> {
			var loanBook = loan.getBook();
			var bookDTO = modelMapper.map(loanBook, BookDTO.class);
			var loanDTO = modelMapper.map(loan, LoanDTO.class);
			loanDTO.setBook(bookDTO);
			return loanDTO;
		}).collect(Collectors.toList());

		return new PageImpl<>(loansDTO, pageable, loans.getTotalElements());

	}

}
