package com.estudos.springboot.libraryapi.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.estudos.springboot.libraryapi.dto.LoanDTO;
import com.estudos.springboot.libraryapi.dto.LoanFilterDTO;
import com.estudos.springboot.libraryapi.dto.ReturnedLoanDTO;
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

		Loan loan = Loan.builder().id(1l).customer("fulano").book(book).loanDate(LocalDate.now()).build();
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

	@Test
	@DisplayName("Deve retornar um livro")
	public void returnBookTest() throws Exception {
		// cenário { returned: true }
		ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
		Loan loan = Loan.builder().id(1l).build();
		BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.of(loan));

		String json = new ObjectMapper().writeValueAsString(dto);

		mvc.perform(MockMvcRequestBuilders.patch(LOAN_API.concat("/1")).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());

		Mockito.verify(loanService, Mockito.times(1)).update(loan);

	}

	@Test
	@DisplayName("Deve retornar 404 quando tentar devolver um livro inexistente")
	public void returnInexistentBookTest() throws Exception {
		// cenário { returned: true }
		ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();

		String json = new ObjectMapper().writeValueAsString(dto);
		BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.empty());

		mvc.perform(MockMvcRequestBuilders.patch(LOAN_API.concat("/1")).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isNotFound());

	}

	@Test
	@DisplayName("Deve filtrar emprestimos")
	void filterLoanTest() throws Exception {
		long id = 1l;
		var book = Book.builder().id(id).title("Title Updated").author("Author Updated").isbn("123").build();
		var loan = Loan.builder().customer("fulano").book(book).loanDate(LocalDate.now()).id(id).build();

		BDDMockito.given(loanService.find(Mockito.any(LoanFilterDTO.class), Mockito.any(Pageable.class)))
				.willReturn(new PageImpl<Loan>(Collections.singletonList(loan), PageRequest.of(0, 10), 1));

		var queryString = String.format("?isbn=%s&custiner=%s&page=0&size=10", book.getIsbn(), loan.getCustomer());

		var request = MockMvcRequestBuilders.get(LOAN_API.concat(queryString)).accept(MediaType.APPLICATION_JSON);

		mvc.perform(request).andExpect(status().isOk()).andExpect(jsonPath("content", Matchers.hasSize(1)))
				.andExpect(jsonPath("totalElements").value(1)).andExpect(jsonPath("pageable.pageSize").value(10))
				.andExpect(jsonPath("pageable.pageNumber").value(0));
	}

}
