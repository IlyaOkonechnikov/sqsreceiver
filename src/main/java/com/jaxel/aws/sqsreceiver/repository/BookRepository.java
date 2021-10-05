package com.jaxel.aws.sqsreceiver.repository;

import com.jaxel.aws.sqsreceiver.model.Book;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@EnableScan
public interface BookRepository extends CrudRepository<Book, String> {

  Optional<Book> findByTitleAndAndAuthor(String title, String author);
}