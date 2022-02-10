package com.estudos.springboot.libraryapi.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.estudos.springboot.libraryapi.dto.LoanDTO;
import com.estudos.springboot.libraryapi.entity.Book;
import com.estudos.springboot.libraryapi.entity.Loan;
import com.estudos.springboot.libraryapi.exception.BusinessException;
import com.estudos.springboot.libraryapi.exception.ObjectNotFoundException;
import com.estudos.springboot.libraryapi.service.BookService;
import com.estudos.springboot.libraryapi.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WebMvcTest(controllers = LoanController.class)
class LoanControllerTest {

	private static final String LOAN_API = "/api/loans";

	@Autowired
	private MockMvc mvc;

	@MockBean
	private BookService service;

	@MockBean
	private LoanService loanService;

	@Test
	@DisplayName("Deve realizar um emprestimo")
	void createLoanTest() throws Exception {
		var dto = LoanDTO.builder().isbn("123").customer("Fulano").build();

		String json = new ObjectMapper().writeValueAsString(dto);

		Book book = Book.builder().id(1l).isbn("123").build();
		BDDMockito.given(service.getBookByIsbn("123")).willReturn(book);

		Loan loan = Loan.builder().id(1l).custumer("fulano").book(book).loanDate(LocalDate.now()).build();
		BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

		var request = MockMvcRequestBuilders.post(LOAN_API).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(request).andExpect(status().isOk()).andExpect(jsonPath("id").value(1l));

	}

	@Test
	@DisplayName("Deve dar erro ao tentar fazer emprestimo de um livro inexistente")
	void invalidIsbnCreateLoan() throws Exception {
		LoanDTO dto = LoanDTO.builder().isbn("1234").customer("Fulano").build();
		String json = new ObjectMapper().writeValueAsString(dto);

		String exceptionMessage = "Book not found for passed isbn";
		BDDMockito.given(service.getBookByIsbn("1234")).willThrow(new ObjectNotFoundException(exceptionMessage));

		var request = MockMvcRequestBuilders.post(LOAN_API).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(request).andExpect(status().isNotFound()).andExpect(jsonPath("errors", Matchers.hasSize(1)))
				.andExpect(jsonPath("errors[0]").value(exceptionMessage));

	}

	@Test
	@DisplayName("Deve dar erro ao tentar fazer emprestimo de um livro emprestado")
	void lonedBookErrorOnCreateLoan() throws Exception {
		LoanDTO dto = LoanDTO.builder().isbn("1234").customer("Fulano").build();
		String json = new ObjectMapper().writeValueAsString(dto);

		Book book = Book.builder().id(1l).isbn("123").build();
		BDDMockito.given(service.getBookByIsbn("123")).willReturn(book);

		BDDMockito.given(loanService.save(Mockito.any(Loan.class)))
				.willThrow(new BusinessException("Book already loadned"));

		var request = MockMvcRequestBuilders.post(LOAN_API).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(request).andExpect(status().isBadRequest()).andExpect(jsonPath("errors", Matchers.hasSize(1)))
				.andExpect(jsonPath("errors[0]").value("Book already loadned"));

	}

}
