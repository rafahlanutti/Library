package com.estudos.springboot.libraryapi.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.estudos.springboot.libraryapi.entity.Book;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
class BookRepositoryTest {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	BookRepository repository;

	@Test
	@DisplayName("Deve retornar verdadeiro quando existir um livro na base com isbn informado")
	void returnTrueWhenIsbnExists() {
		String isbn = "123";
		persistBook(createBook(isbn));
		var exists = repository.existsByIsbn(isbn);
		assertTrue(exists);
	}

	private Book persistBook(Book book) {
		return entityManager.persist(book);
	}

	private Book createBook(String isbn) {
		return Book.builder().author("Rafael").title("Estudo com lombok").isbn(isbn).build();
	}

	@Test
	@DisplayName("Deve retornar falso quando não existir um livro na base com isbn informado")
	void returnFalseWhenIsbnExists() {
		String isbn = "123";
		var exists = repository.existsByIsbn(isbn);
		assertFalse(exists);
	}

	@Test
	@DisplayName("Deve retornar um livro por id")
	void getById() {
		var created = persistBook(createBook("123"));

		var finded = repository.findById(created.getId());
		assertTrue(finded.isPresent());
		assertEquals(created.getId(), finded.get().getId());

	}

	@Test
	@DisplayName("Deve salvar um livro")
	void saveBook() {
		var book = createBook("123");
		var saved = repository.save(book);

		assertNotNull(saved.getId());
	}

	@Test
	@DisplayName("Deve Atualizar um livro")
	void updateBook() {
		var book = createBook("123");
		repository.save(book);

		book.setAuthor("Author Updated");
		var bookUpdated = repository.save(book);

		assertEquals(book.getAuthor(), bookUpdated.getAuthor());
		assertEquals(book.getId(), bookUpdated.getId());
	}

	@Test
	@DisplayName("Deve deletar um livro")
	void deleteTest() {
		var book = createBook("123");
		repository.save(book);

		repository.delete(book);

		var finded = repository.findById(book.getId());

		assertTrue(finded.isEmpty());
	}

}
