package com.estudos.springboot.libraryapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.estudos.springboot.libraryapi.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

	boolean existsByIsbn(String isbn);

}
