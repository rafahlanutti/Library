package com.estudos.springboot.libraryapi.service;

import com.estudos.springboot.libraryapi.entity.Book;

public interface BookService {

	public Book save(Book dto);

	public Book getById(long id);

	public void delete(Long id);

	public Book update(Book book);
}
