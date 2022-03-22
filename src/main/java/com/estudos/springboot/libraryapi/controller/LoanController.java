package com.estudos.springboot.libraryapi.controller;

import java.time.LocalDate;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.estudos.springboot.libraryapi.dto.BookDTO;
import com.estudos.springboot.libraryapi.dto.LoanDTO;
import com.estudos.springboot.libraryapi.dto.LoanFilterDTO;
import com.estudos.springboot.libraryapi.dto.ReturnedLoanDTO;
import com.estudos.springboot.libraryapi.entity.Loan;
import com.estudos.springboot.libraryapi.service.BookService;
import com.estudos.springboot.libraryapi.service.LoanService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

	private final LoanService service;
	private final BookService bookService;
	private final ModelMapper modelMapper;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<LoanDTO> create(@RequestBody LoanDTO dto) {
		var book = bookService.getBookByIsbn(dto.getIsbn());

		Loan entity = Loan.builder().book(book).customer(dto.getCustomer()).loanDate(LocalDate.now()).build();

		entity = service.save(entity);

		return ResponseEntity.ok(modelMapper.map(entity, LoanDTO.class));
	}

	@PatchMapping("{id}")
	public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto) {
		Loan loan = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		loan.setReturned(dto.getReturned());
		service.update(loan);
	}

	@GetMapping
	public Page<LoanDTO> find(LoanFilterDTO dto, Pageable pageRequest) {
		Page<Loan> result = service.find(dto, pageRequest);
		var loans = result.getContent().stream().map(entity -> {
			var book = entity.getBook();
			BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
			LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class);
			loanDTO.setBook(bookDTO);
			return loanDTO;
		}).collect(Collectors.toList());

		return new PageImpl<>(loans, pageRequest, result.getTotalElements());

	}

}
