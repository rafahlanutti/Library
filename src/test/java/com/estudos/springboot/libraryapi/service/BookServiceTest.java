package com.estudos.springboot.libraryapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;

import java.util.Optional;

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
import com.estudos.springboot.libraryapi.exception.ObjectNotFoundException;
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

		var book = createBook(null);
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
		Mockito.when(repository.save(book))
				.thenReturn(Book.builder().id(1l).author("Author").title("title").isbn("123123").build());

		var saved = service.save(book);

		assertNotNull(saved.getId());
		assertEquals(book.getIsbn(), saved.getIsbn());
		assertEquals(book.getAuthor(), saved.getAuthor());
		assertEquals(book.getTitle(), saved.getTitle());
	}

	private Book createBook(Long id) {
		return Book.builder().id(id).isbn("123123").author("Author").title("title").build();
	}

	@Test
	@DisplayName("Deve lançar um erro de negocio ao tentar salvar um livro com isbn duplicado")
	void shouldNotSaveBookABookWithDuplicateISBN() {
		Book createBook = createBook(null);

		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
		var exception = assertThrows(BusinessException.class, () -> {
			service.save(createBook);
		});
		assertEquals("Isbn já cadastrado", exception.getMessage());

		Mockito.verify(repository, Mockito.never()).save(createBook);

	}

	@Test
	@DisplayName("Deve obter um livro por id")
	void getById() {
		var book = createBook(1l);
		Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(book));

		var finded = service.getById(1l);

		assertEquals(book.getId(), finded.getId());
		assertEquals(book.getAuthor(), finded.getAuthor());
		assertEquals(book.getIsbn(), finded.getIsbn());
		assertEquals(book.getTitle(), finded.getTitle());

	}

	@Test
	@DisplayName("Não deve obter um livro por id")
	void shouldNotGetById() {
		Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

		assertThrows(ObjectNotFoundException.class, () -> service.getById(1l));
	}

	@Test
	@DisplayName("Deve atualizar um livro")
	void shouldUpdateBook() {

		var book = createBook(1l);
		Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(book));
		var bookUpdate = createBook(1l);
		bookUpdate.setTitle("Title updated");
		Mockito.when(repository.save(Mockito.any())).thenReturn(bookUpdate);
		Book updated = service.update(bookUpdate);

		assertEquals(book.getId(), updated.getId());
		assertEquals(book.getIsbn(), updated.getIsbn());
		assertNotEquals(book.getTitle(), updated.getTitle());
	}

	@Test
	@DisplayName("Não deve atualizar um livro")
	void shouldNotUpdateBook() {

		Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
		var bookUpdate = createBook(1l);
		var ex = assertThrows(ObjectNotFoundException.class, () -> service.update(bookUpdate));
		assertEquals("404 NOT_FOUND \"Objeto n�o encontrado\"", ex.getMessage());

	}

	@Test
	@DisplayName("Deve deletar um livro")
	void shouldDeleteBook() {

		var book = createBook(1l);

		Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(book));
		doNothing().when(repository).delete(book);
		service.delete(1l);

		Mockito.verify(repository, Mockito.times(1)).delete(book);
		Mockito.verify(repository, Mockito.times(1)).findById(book.getId());

	}

	@Test
	@DisplayName("Não deve deletar um livro")
	void shouldNotDeleteBook() {

		Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
		var ex = assertThrows(ObjectNotFoundException.class, () -> service.delete(1l));
		assertEquals("404 NOT_FOUND \"Objeto n�o encontrado\"", ex.getMessage());

	}
}
