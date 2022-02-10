package com.estudos.springboot.libraryapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.estudos.springboot.libraryapi.entity.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {

}
