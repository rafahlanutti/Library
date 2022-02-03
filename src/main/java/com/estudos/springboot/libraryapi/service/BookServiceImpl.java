package com.estudos.springboot.libraryapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.estudos.springboot.libraryapi.entity.Book;
import com.estudos.springboot.libraryapi.exception.BusinessException;
import com.estudos.springboot.libraryapi.exception.ObjectNotFoundException;
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
	public Book getById(long id) {

		return repository.findById(id)
				.orElseThrow(() -> new ObjectNotFoundException(HttpStatus.NOT_FOUND, "Objeto não encontrado"));

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

}
