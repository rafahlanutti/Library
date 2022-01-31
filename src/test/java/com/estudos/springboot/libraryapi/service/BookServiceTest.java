package com.estudos.springboot.libraryapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.estudos.springboot.libraryapi.entity.Book;
import com.estudos.springboot.libraryapi.exception.BusinessException;
import com.estudos.springboot.libraryapi.repository.BookRepository;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

	private BookService service;

	@MockBean
	private BookRepository repository;

	@BeforeEach
	public void setup() {
		this.service = new BookServiceImpl(repository);
	}

	@Test
	@DisplayName("Deve salvar um livro")
	void saveBookTest() {

		var book = createBook();
		Mockito.when(repository.existByIsbn(Mockito.anyString())).thenReturn(false);
		Mockito.when(repository.save(book))
				.thenReturn(Book.builder().id(1l).author("Author").title("title").isbn("123123").build());

		var saved = service.save(book);

		assertNotNull(saved.getId());
		assertEquals(book.getIsbn(), saved.getIsbn());
		assertEquals(book.getAuthor(), saved.getAuthor());
		assertEquals(book.getTitle(), saved.getTitle());
	}

	private Book createBook() {
		return Book.builder().isbn("123123").author("Author").title("title").build();
	}

	@Test
	@DisplayName("Deve lançar um erro de negocio ao tentar salvar um livro com isbn duplicado")
	void shouldNotSaveBookABookWithDuplicateISBN() {
		Book createBook = createBook();

		Mockito.when(repository.existByIsbn(Mockito.anyString())).thenReturn(true);
		var exception = assertThrows(BusinessException.class, () -> {
			service.save(createBook);
		});
		assertEquals("Isbn já cadastrado", exception.getMessage());

		Mockito.verify(repository, Mockito.never()).save(createBook);

	}
}
