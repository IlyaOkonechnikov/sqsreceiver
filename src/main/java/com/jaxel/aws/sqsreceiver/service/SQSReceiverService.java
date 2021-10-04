package com.jaxel.aws.sqsreceiver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaxel.aws.sqsreceiver.model.Book;
import com.jaxel.aws.sqsreceiver.repository.BookRepository;

import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SQSReceiverService {

  private final ObjectMapper mapper;
  private final BookRepository repository;

  @SqsListener(value = "${cloud.aws.queue.name}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
  public void receiveMessage(String payload) throws JsonProcessingException {
    log.info("The message was successfully received. payload: {}", payload);
    final Book book = mapper.readValue(payload, Book.class);
    if (repository.findByTitleAndAndAuthor(book.getTitle(), book.getAuthor()).isEmpty()) {
      final Book savedBook = repository.save(book);
      log.info("The book was successfully saved. id: {}", savedBook.getId());
    } else {
      log.info("There is already a book by the author {} with the title {}.", book.getTitle(), book.getAuthor());
    }
  }
}