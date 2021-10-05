package com.jaxel.aws.sqsreceiver;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaxel.aws.sqsreceiver.service.SQSReceiverService;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import lombok.SneakyThrows;


@Testcontainers
@SpringBootTest(classes = BaseTest.TestConfig.class)
@ContextConfiguration(initializers = BaseTest.ContextInitializer.class)
public abstract class BaseTest {

  protected static final int DYNAMO_DB_PORT = 8000;

  @Container
  protected static GenericContainer<?> DYNAMO_DB =
      new GenericContainer<>("amazon/dynamodb-local:latest")
          .withExposedPorts(DYNAMO_DB_PORT);

  public static class ContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    @SneakyThrows
    public void initialize(ConfigurableApplicationContext ctx) {
      TestPropertyValues.of(
              String.format("application.dynamodb.endpoint: http://%s:%s",
                  DYNAMO_DB.getContainerIpAddress(), DYNAMO_DB.getMappedPort(DYNAMO_DB_PORT)))
          .applyTo(ctx);
    }
  }

  @Configuration
  @Import({ObjectMapper.class, SQSReceiverService.class})
  @EnableDynamoDBRepositories("com.jaxel.aws.sqsreceiver.repository")
  static class TestConfig {

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
      return AmazonDynamoDBClientBuilder.standard().build();
    }
  }
}
