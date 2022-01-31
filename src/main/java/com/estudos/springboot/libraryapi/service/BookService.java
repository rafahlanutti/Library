package com.estudos.springboot.libraryapi.service;

import java.util.Optional;

import com.estudos.springboot.libraryapi.entity.Book;

public interface BookService {

	public Book save(Book dto);

	public Optional<Book> getById(long id);
}
