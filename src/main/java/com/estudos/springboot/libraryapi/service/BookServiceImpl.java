package com.estudos.springboot.libraryapi.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.estudos.springboot.libraryapi.entity.Book;
import com.estudos.springboot.libraryapi.exception.BusinessException;
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

		if (repository.existsByIsbn(book.getIsbn())) {
			throw new BusinessException("Isbn já cadastrado");
		}

		return repository.save(book);
	}

	@Override
	public Optional<Book> getById(long id) {
		return null;
	}

}
