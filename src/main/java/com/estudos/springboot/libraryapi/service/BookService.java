package com.estudos.springboot.libraryapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.estudos.springboot.libraryapi.entity.Book;

public interface BookService {

	public Book save(Book dto);

	public Book getById(long id);

	public void delete(Long id);

	public Book update(Book book);

	public Page<Book> find(Book filter, Pageable pageable);
}
