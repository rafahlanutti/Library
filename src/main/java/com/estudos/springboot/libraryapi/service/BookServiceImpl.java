package com.estudos.springboot.libraryapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.estudos.springboot.libraryapi.entity.Book;
import com.estudos.springboot.libraryapi.exception.BusinessException;
import com.estudos.springboot.libraryapi.exception.ObjectNotFoundException;
import com.estudos.springboot.libraryapi.messages.Messages;
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
			throw new BusinessException(Messages.ISNB_NOT_ALLOW.toString());
		}

		return repository.save(book);
	}

	@Override
	public Book getById(long id) {

		return repository.findById(id).orElseThrow(() -> new ObjectNotFoundException(Messages.NOT_FOUND.toString()));

	}

	@Override
	public void delete(Long id) {

		var book = this.getById(id);
		this.repository.delete(book);

	}

	@Override
	public Book update(Book updated) {
		this.getById(updated.getId());
		return this.repository.save(updated);

	}

	@Override
	public Page<Book> find(Book filter, Pageable pageable) {
		Example<Book> example = Example.of(filter, ExampleMatcher.matching().withIgnoreCase().withIgnoreNullValues()
				.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
		return repository.findAll(example, pageable);
	}

	@Override
	public Book getBookByIsbn(String string) {
		return this.repository.findById(1l)
				.orElseThrow(() -> new ObjectNotFoundException(Messages.NOT_FOUND.toString()));
	}

}
