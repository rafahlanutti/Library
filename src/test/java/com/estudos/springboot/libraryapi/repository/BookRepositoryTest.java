package com.estudos.springboot.libraryapi.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
		createBook(isbn);
		var exists = repository.existsByIsbn(isbn);
		assertTrue(exists);
	}

	private Book createBook(String isbn) {
		return entityManager.persist(Book.builder().author("Rafael").title("Estudo com lombok").isbn(isbn).build());
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
		var created = createBook("123");

		var finded = repository.findById(created.getId());
		assertTrue(finded.isPresent());
		assertEquals(created.getId(), finded.get().getId());

	}

}
