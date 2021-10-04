package com.jaxel.aws.sqsreceiver;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaxel.aws.sqsreceiver.service.SQSReceiverService;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@Testcontainers
@SpringBootTest
class SQSReceiverServiceTest {

  private static final String REGION = "eu-central-1";
  private static final String QUEUE_NAME = "testBookInfoQueue";
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Container
  private static final LocalStackContainer CONTAINER =
      new LocalStackContainer(DockerImageName.parse("localstack/localstack:0.10.0"))
          .withServices(SQS)
          .withEnv("DEFAULT_REGION", REGION);

  @Autowired
  private SQSReceiverService service;
  @Autowired
  private AmazonSQSAsync amazonSQS;

  @BeforeAll
  static void beforeAll() throws IOException, InterruptedException {
    CONTAINER.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", QUEUE_NAME);
  }

  @TestConfiguration
  static class AwsTestConfig {

    @Bean
    public AmazonSQSAsync amazonSQS() {
      return AmazonSQSAsyncClientBuilder.standard()
          .withCredentials(CONTAINER.getDefaultCredentialsProvider())
          .withEndpointConfiguration(CONTAINER.getEndpointConfiguration(SQS))
          .build();
    }
  }

//  @Test
//  void whenSendMessage_thenSuccess() throws JsonProcessingException {
//    Book book = new Book("War and Peace", "Leo Tolstoy");
//    service.send(book);
//    String receivedMessageBody = amazonSQS.receiveMessage(amazonSQS.getQueueUrl(QUEUE_NAME).getQueueUrl()).getMessages().get(0).getBody();
//    Book receivedBook = MAPPER.readValue(receivedMessageBody, Book.class);
//    assertEquals(receivedBook, book);
//  }
}
