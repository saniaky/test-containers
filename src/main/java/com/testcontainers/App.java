package com.testcontainers;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.KINESIS;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import org.apache.log4j.BasicConfigurator;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;

public class App {

  private static final String REGION = "us-east-2";

  public static void main(String[] args) {
    // Init Log4j
    BasicConfigurator.configure();

    LocalStackContainer localStack = new LocalStackContainer("0.11.5")
        .withServices(Service.KINESIS, Service.LAMBDA)
        .withEnv("DEFAULT_REGION", "us-east-2");
    localStack.start();

    // Create Kinesis Client
    var endpointConfiguration = new EndpointConfiguration(
        localStack.getEndpointConfiguration(KINESIS).getServiceEndpoint(),
        REGION);

    var basicAWSCredentials = new BasicAWSCredentials("", "");
    var credentialsProvider = new AWSStaticCredentialsProvider(basicAWSCredentials);
    AmazonKinesis kinesis = AmazonKinesisClientBuilder
        .standard()
        .withCredentials(credentialsProvider)
        .withEndpointConfiguration(endpointConfiguration)
        .build();

    // Work with kinesis
    kinesis.putRecord("streamName", null, "p1");
  }

}
