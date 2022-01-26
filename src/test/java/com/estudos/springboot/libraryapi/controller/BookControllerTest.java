package com.estudos.springboot.libraryapi.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
	void createInvalidBookTest() {

	}

}
