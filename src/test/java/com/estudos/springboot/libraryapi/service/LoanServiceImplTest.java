package com.estudos.springboot.libraryapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.estudos.springboot.libraryapi.entity.Book;
import com.estudos.springboot.libraryapi.entity.Loan;
import com.estudos.springboot.libraryapi.exception.BusinessException;
import com.estudos.springboot.libraryapi.repository.LoanRepository;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class LoanServiceImplTest {

	@MockBean
	LoanRepository repository;

	LoanService service;

	@BeforeEach
	public void setup() {
		this.service = new LoanServiceImpl(repository);
	}

	@Test
	void saveLoanTest() {
		var book = Book.builder().id(1l).isbn("123").build();
		var loan = Loan.builder().custumer("fulano").book(book).loanDate(LocalDate.now()).build();

		var savedLoanReturn = Loan.builder().id(1l).custumer("fulano").book(book).loanDate(LocalDate.now()).build();

		when(repository.save(loan)).thenReturn(savedLoanReturn);

		var saved = service.save(loan);

		assertEquals(savedLoanReturn.getId(), saved.getId());
	}

	@Test
	@DisplayName("Deve Lançar erro de negócio ao salvar um emprestimo com livro já emprestado")
	void loanedBookSaveTest() {
		var book = Book.builder().id(1l).isbn("123").build();
		var loan = Loan.builder().custumer("fulano").book(book).loanDate(LocalDate.now()).build();

		when(repository.existsByBookAndNotReturned(book)).thenReturn(true);
		
		var exeption = assertThrows(BusinessException.class, () -> service.save(loan));
		assertEquals("Livro já emprestado", exeption.getMessage());

	}
}
