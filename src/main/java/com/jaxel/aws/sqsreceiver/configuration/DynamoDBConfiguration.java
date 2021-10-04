package com.jaxel.aws.sqsreceiver.configuration;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDynamoDBRepositories("com.jaxel.aws.sqsreceiver.repository")
public class DynamoDBConfiguration {

  @Value("${cloud.aws.credentials.access-key}")
  private String awsAccessKey;

  @Value("${cloud.aws.credentials.secret-key}")
  private String awsSecretKey;

  @Bean
  public AmazonDynamoDB amazonDynamoDB() {
    return AmazonDynamoDBClientBuilder.standard()
        .withCredentials(awsCredentialsProvider())
        .build();
  }

  private AWSCredentialsProvider awsCredentialsProvider(){
    return new AWSStaticCredentialsProvider(
        new BasicAWSCredentials(awsAccessKey, awsSecretKey));
  }
}