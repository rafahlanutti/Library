package com.estudos.springboot.libraryapi.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.estudos.springboot.libraryapi.dto.BookDTO;
import com.estudos.springboot.libraryapi.entity.Book;
import com.estudos.springboot.libraryapi.exception.BusinessException;
import com.estudos.springboot.libraryapi.exception.ObjectNotFoundException;
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
		BDDMockito.given(service.getById(id)).willReturn(book);

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

		BDDMockito.given(service.getById(Mockito.anyLong()))
				.willThrow(new ObjectNotFoundException(HttpStatus.NOT_FOUND, "Objeto não encontrado"));
		var request = MockMvcRequestBuilders.get(BOOK_API.concat("/" + id)).accept(MediaType.APPLICATION_JSON);
		mvc.perform(request).andExpect(status().isNotFound());

	}

	@Test
	@DisplayName("Deve deletar um livro")
	void deleteBookTest() throws Exception {
		var book = Book.builder().id(1l).title("Title").author("Author").isbn("123").build();
		BDDMockito.given(service.getById(1l)).willReturn(book);

		var request = MockMvcRequestBuilders.delete(BOOK_API.concat("/" + 1l)).accept(MediaType.APPLICATION_JSON);
		mvc.perform(request).andExpect(status().isNoContent());

	}

	@Test
	@DisplayName("Deve retornar um erro quando deletar um livro que não existe.")
	void dontHaveDeleteBookTest() throws Exception {

		Mockito.doThrow(new ObjectNotFoundException(HttpStatus.NOT_FOUND, "Objeto não encontrado")).when(service)
				.delete(Mockito.anyLong());

		var request = MockMvcRequestBuilders.delete(BOOK_API.concat("/" + 1l)).accept(MediaType.APPLICATION_JSON);
		mvc.perform(request).andExpect(status().isNotFound());

	}

	@Test
	@DisplayName("Deve atualizar um livro")
	void updateBookTest() throws Exception {
		var id = 1l;

		var updated = Book.builder().id(id).title("Title Updated").author("Author Updated").isbn("123").build();

		BDDMockito.given(service.update(Mockito.any())).willReturn(updated);

		var json = new ObjectMapper().writeValueAsBytes(updated);

		var request = MockMvcRequestBuilders.put(BOOK_API.concat("/" +id)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).content(json);


		mvc.perform(request).andExpect(status().isOk()).andExpect(jsonPath("id").value(1l))
				.andExpect(jsonPath("title").value(updated.getTitle()))
				.andExpect(jsonPath("author").value(updated.getAuthor()))
				.andExpect(jsonPath("isbn").value(updated.getIsbn()));

	}

	@Test
	@DisplayName("Deve retornar 404 ao tentar atualizar um livro")
	void dontHaveUpdateBookTest() throws Exception {
		var book = Book.builder().id(1l).title("Title Updated").author("Author Updated").isbn("123").build();

		BDDMockito.given(service.update(Mockito.any()))
				.willThrow(new ObjectNotFoundException(HttpStatus.NOT_FOUND, "Objeto não encontrado"));

		var json = new ObjectMapper().writeValueAsBytes(book);

		var request = MockMvcRequestBuilders.put(BOOK_API.concat("/" +1l)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(request).andExpect(status().isNotFound());

	}
}
