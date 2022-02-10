package com.estudos.springboot.libraryapi.controller;

import java.time.LocalDate;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.estudos.springboot.libraryapi.dto.LoanDTO;
import com.estudos.springboot.libraryapi.entity.Loan;
import com.estudos.springboot.libraryapi.service.BookService;
import com.estudos.springboot.libraryapi.service.LoanService;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

	private LoanService loanService;
	private BookService bookService;
	private ModelMapper modelMapper;

	@Autowired
	public LoanController(LoanService loanService, BookService bookService, ModelMapper modelMapper) {
		this.loanService = loanService;
		this.bookService = bookService;
		this.modelMapper = modelMapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<LoanDTO> create(@RequestBody LoanDTO dto) {
		var book = bookService.getBookByIsbn(dto.getIsbn());

		Loan entity = Loan.builder().book(book).custumer(dto.getCustomer()).loanDate(LocalDate.now()).build();

		entity = loanService.save(entity);

		return ResponseEntity.ok(modelMapper.map(entity, LoanDTO.class));
	}

}
