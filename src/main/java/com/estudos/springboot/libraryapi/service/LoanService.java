package com.estudos.springboot.libraryapi.service;

import java.util.Optional;

import com.estudos.springboot.libraryapi.entity.Loan;

public interface LoanService {

	Loan save(Loan any);

	Optional<Loan> getById(Long id);

	Loan update(Loan loan);

}
