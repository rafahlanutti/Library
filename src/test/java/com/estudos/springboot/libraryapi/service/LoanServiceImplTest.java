package com.estudos.springboot.libraryapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.estudos.springboot.libraryapi.dto.LoanFilterDTO;
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
		var loan = Loan.builder().customer("fulano").book(book).loanDate(LocalDate.now()).build();

		var savedLoanReturn = Loan.builder().id(1l).customer("fulano").book(book).loanDate(LocalDate.now()).build();

		when(repository.save(loan)).thenReturn(savedLoanReturn);

		var saved = service.save(loan);

		assertEquals(savedLoanReturn.getId(), saved.getId());
	}

	@Test
	@DisplayName("Deve Lançar erro de negócio ao salvar um emprestimo com livro já emprestado")
	void loanedBookSaveTest() {
		var book = Book.builder().id(1l).isbn("123").build();
		var loan = Loan.builder().customer("fulano").book(book).loanDate(LocalDate.now()).build();

		when(repository.existsByBookAndNotReturned(book)).thenReturn(true);

		var exeption = assertThrows(BusinessException.class, () -> service.save(loan));
		assertEquals("Livro já emprestado", exeption.getMessage());

	}

	@Test
	@DisplayName("Deve obter as informações de um emprestimo pelo ID")
	void getLoanDetailTest() {
		long id = 1l;
		var loan = createLoan();

		when(repository.findById(id)).thenReturn(Optional.of(loan));

		var result = service.getById(id);

		assertTrue(result.isPresent());
		assertEquals(loan.getCustomer(), result.get().getCustomer());

	}

	@Test
	@DisplayName("Deve atualizar um objeto")
	void updateLoanDetailTest() {
		var loan = createLoan();

		when(repository.save(loan)).thenReturn(loan);

		var result = service.update(loan);

		assertTrue(result.getReturned());

		verify(repository).save(loan);

	}

	private Loan createLoan() {
		long id = 1l;
		var book = Book.builder().id(id).isbn("123").build();
		var loan = Loan.builder().customer("fulano").book(book).loanDate(LocalDate.now()).id(id).returned(true).build();
		return loan;
	}

	@Test
	@DisplayName("Deve filtrar emprestimos pelas propriedades")
	void findLoanTest() {

		var loan = createLoan();

		var pageRequest = PageRequest.of(0, 10);
		List<Loan> lista = Collections.singletonList(loan);
		var page = new PageImpl<Loan>(lista, pageRequest, 1);

		Mockito.when(repository.findByBookIsbnOrCustomer(Mockito.anyString(), Mockito.anyString(),
				Mockito.any(PageRequest.class))).thenReturn(page);

		LoanFilterDTO dto = LoanFilterDTO.builder().customer("Fulano").isbn("321").build();

		var result = service.find(dto, pageRequest);

		assertEquals(1, result.getTotalElements());
		assertEquals(lista, result.getContent());
		assertEquals(0, result.getPageable().getPageNumber());
		assertEquals(10, result.getPageable().getPageSize());

	}
}
