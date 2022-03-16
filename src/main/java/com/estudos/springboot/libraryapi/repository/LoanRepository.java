package com.estudos.springboot.libraryapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.estudos.springboot.libraryapi.entity.Book;
import com.estudos.springboot.libraryapi.entity.Loan;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

	@Query(value = "SELECT case WHEN (count(l.id ) > 0 ) then true else false end from Loan l where l.book = :book and"
			+ "( l.returned is null or l.returned is false )")
	boolean existsByBookAndNotReturned(@Param("book") Book book);

}
