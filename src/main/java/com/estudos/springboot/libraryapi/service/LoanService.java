package com.estudos.springboot.libraryapi.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.estudos.springboot.libraryapi.dto.LoanFilterDTO;
import com.estudos.springboot.libraryapi.entity.Loan;

public interface LoanService {

	Loan save(Loan any);

	Optional<Loan> getById(Long id);

	Loan update(Loan loan);

	Page<Loan> find(LoanFilterDTO filter, Pageable page);

}
