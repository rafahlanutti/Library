package com.estudos.springboot.libraryapi.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.estudos.springboot.libraryapi.dto.BookDTO;
import com.estudos.springboot.libraryapi.entity.Book;
import com.estudos.springboot.libraryapi.exception.BusinessException;
import com.estudos.springboot.libraryapi.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
class BookControllerTest {

	@Autowired
	MockMvc mvc;

	@MockBean
	BookService service;

	static String BOOK_API = "/api/books";
	private BookDTO dto = BookDTO.builder().author("Rafael").title("Estudo com lombok").isbn("123123").build();

	@Test
	@DisplayName("Deve criar um livro com sucesso")
	void createBookTest() throws Exception {

		String json = new ObjectMapper().writeValueAsString(dto);

		var savedBook = Book.builder().id(10l).author("Rafael").title("Estudo com lombok").isbn("123123").build();

		BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);

		var request = MockMvcRequestBuilders.post(BOOK_API).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(request).andExpect(status().isCreated()).andExpect(jsonPath("id").isNotEmpty())
				.andExpect(jsonPath("title").value(dto.getTitle())).andExpect(jsonPath("author").value(dto.getAuthor()))
				.andExpect(jsonPath("isbn").value(dto.getIsbn()));

	}

	@Test
	@DisplayName("Deve lançar erro de validação")
	void createInvalidBookTest() throws Exception {

		String json = new ObjectMapper().writeValueAsString(new BookDTO());

		var request = MockMvcRequestBuilders.post(BOOK_API).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(request).andExpect(status().isBadRequest()).andExpect(jsonPath("errors", Matchers.hasSize(3)));

	}

	@Test
	@DisplayName("Deve lançar erro ao criar livro com isbn já cadastrado")
	void shouldThrowExceptionWhenIsbnIsExistInDatabase() throws Exception {

		String json = new ObjectMapper().writeValueAsString(dto);

		String exceptionMessage = "Isbn já cadastrado";
		BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BusinessException(exceptionMessage));

		var request = MockMvcRequestBuilders.post(BOOK_API).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(request).andExpect(status().isBadRequest()).andExpect(jsonPath("errors", Matchers.hasSize(1)))
				.andExpect(jsonPath("errors[0]").value(exceptionMessage));

	}

	@Test
	@DisplayName("Deve obter informacoes de um livro")
	void getBookDetailsTest() throws Exception {

		var id = 1l;

		var book = Book.builder().id(id).title("Title").author("Author").isbn("123").build();
		BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

		var request = MockMvcRequestBuilders.get(BOOK_API.concat("/" + id)).accept(MediaType.APPLICATION_JSON);

		mvc.perform(request).andExpect(jsonPath("id").value(1l)).andExpect(jsonPath("id").value(1l))
				.andExpect(jsonPath("title").value(book.getTitle()))
				.andExpect(jsonPath("author").value(book.getAuthor()))
				.andExpect(jsonPath("isbn").value(book.getIsbn()));

	}

	@Test
	@DisplayName("Deve retornar resource not found quando o livro procurado nao existir")
	void bookNotFoundTest() throws Exception {

		var id = 1l;

		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
		var request = MockMvcRequestBuilders.get(BOOK_API.concat("/" + id)).accept(MediaType.APPLICATION_JSON);
		mvc.perform(request).andExpect(status().isNotFound());

	}
}
