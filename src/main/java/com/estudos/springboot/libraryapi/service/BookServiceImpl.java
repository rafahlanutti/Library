package com.estudos.springboot.libraryapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.estudos.springboot.libraryapi.entity.Book;
import com.estudos.springboot.libraryapi.repository.BookRepository;

@Service
public class BookServiceImpl implements BookService {

	private BookRepository repository;

	@Autowired
	public BookServiceImpl(BookRepository repository) {
		this.repository = repository;
	}

	@Override
	public Book save(Book book) {
		return repository.save(book);
	}

}
