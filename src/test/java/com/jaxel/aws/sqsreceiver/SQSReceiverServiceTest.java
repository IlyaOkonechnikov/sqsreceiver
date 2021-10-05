package com.jaxel.aws.sqsreceiver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaxel.aws.sqsreceiver.model.Book;
import com.jaxel.aws.sqsreceiver.repository.BookRepository;
import com.jaxel.aws.sqsreceiver.service.SQSReceiverService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SQSReceiverServiceTest extends BaseTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Autowired
  private SQSReceiverService service;
  @Autowired
  private BookRepository repository;

  @Test
  void whenSendNewMessage_thenMessageShouldBeSaved() throws JsonProcessingException {
    String id = "1";
    Book book = new Book(id, "War and Peace", "Leo Tolstoy");
    service.receiveMessage(MAPPER.writeValueAsString(book));
    assertThat(repository.findById(id)).isPresent();
  }

  @Test
  void whenSendExistedMessage_thenMessageShouldNotBeSaved() throws JsonProcessingException {
    String id = "2";
    Book book = new Book(id, "The Gadfly", "Ethel Voynich");
    service.receiveMessage(MAPPER.writeValueAsString(book));
    long booksCount = repository.count();
    service.receiveMessage(MAPPER.writeValueAsString(book));
    assertThat(booksCount).isEqualTo(repository.count());
    assertThat(repository.findById(id)).isPresent();
  }
}
