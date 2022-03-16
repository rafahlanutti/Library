package com.estudos.springboot.libraryapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
		assertEquals("Objeto n�o encontrado", ex.getMessage());
		Mockito.verify(repository, Mockito.never()).save(createBook(1l));

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
		assertEquals("Objeto n�o encontrado", ex.getMessage());
		Mockito.verify(repository, Mockito.never()).delete(createBook(1l));

	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Deve filtrar livro")
	void findBookTest() {

		var book = createBook(1l);
		var pageRequest = PageRequest.of(0, 10);
		List<Book> lista = Collections.singletonList(book);
		var page = new PageImpl<Book>(lista, pageRequest, 1);

		Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class))).thenReturn(page);

		var result = service.find(book, pageRequest);

		assertEquals(1, result.getTotalElements());
		assertEquals(lista, result.getContent());
		assertEquals(0, result.getPageable().getPageNumber());
		assertEquals(10, result.getPageable().getPageSize());

	}

	@Test
	@DisplayName("Deve obter um book pelo isbn")
	void getBookIsbnTest() {

		var book = createBook(1l);
		Mockito.when(repository.findByIsbn(Mockito.anyString())).thenReturn(Optional.of(book));

		var finded = service.getBookByIsbn(book.getIsbn());

		assertEquals(book.getId(), finded.getId());
		assertEquals(book.getAuthor(), finded.getAuthor());
		assertEquals(book.getIsbn(), finded.getIsbn());

		Mockito.verify(repository, times(1)).findByIsbn(Mockito.anyString());

	}
}
