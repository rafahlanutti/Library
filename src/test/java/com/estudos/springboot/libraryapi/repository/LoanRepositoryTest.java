package com.estudos.springboot.libraryapi.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.estudos.springboot.libraryapi.entity.Book;
import com.estudos.springboot.libraryapi.entity.Loan;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
class LoanRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private LoanRepository repository;

	@Test
	@DisplayName("Deve verificar se existe emprestimo não devolvido para o livro.")
	void existsByBookAndNotReturnedTest() {
		var book = createBook("123");
		entityManager.persist(book);

		Loan loan = Loan.builder().book(book).custumer("Fulano").loanDate(LocalDate.now()).build();
		entityManager.persist(loan);

		boolean exists = repository.existsByBookAndNotReturned(book);
		assertTrue(exists);
	}

	private Book createBook(String isbn) {
		return Book.builder().author("Rafael").title("Estudo com lombok").isbn(isbn).build();
	}

}
