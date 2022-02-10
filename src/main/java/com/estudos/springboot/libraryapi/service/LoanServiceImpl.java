package com.estudos.springboot.libraryapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.estudos.springboot.libraryapi.entity.Loan;
import com.estudos.springboot.libraryapi.repository.LoanRepository;

@Service
public class LoanServiceImpl implements LoanService {

	private LoanRepository repository;

	@Autowired
	public LoanServiceImpl(LoanRepository repository) {
		this.repository = repository;
	}

	@Override
	public Loan save(Loan loan) {
		return repository.save(loan);
	}

}
