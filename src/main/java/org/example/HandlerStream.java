package org.example;


import com.amazon.sqs.javamessaging.AmazonSQSExtendedClient;
import com.amazon.sqs.javamessaging.ExtendedClientConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;


// Handler value: example.HandlerStream
public class HandlerStream implements RequestStreamHandler {

  @Override
  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException
  {
    LambdaLogger logger = context.getLogger();
    String S3_BUCKET_NAME = "Bucket Name";

    try
    {
      logger.log("Configurando o bucket\n");

      final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();

      logger.log("Configurando o extended\n");
      final ExtendedClientConfiguration extendedClientConfig =
              new ExtendedClientConfiguration().withLargePayloadSupportEnabled(s3, S3_BUCKET_NAME);

      final AmazonSQS sqsExtended =
              new AmazonSQSExtendedClient(AmazonSQSClientBuilder
                      .defaultClient(), extendedClientConfig);

      logger.log("Gerando o arquivo\n");
      int stringLength = 300000;
      char[] chars = new char[stringLength];
      Arrays.fill(chars, 'z');
      final String myLongString = new String(chars);

      final String myQueueUrl = "https://sqs.us-east-1.amazonaws.com/ID ACCOUNT/your SQS name";

      // Send the message.
      final SendMessageRequest myMessageRequest = new SendMessageRequest(myQueueUrl, myLongString);
      sqsExtended.sendMessage(myMessageRequest);
      logger.log("Sent the message\n");

      // Receive the message.
      final ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
      List<Message> messages = sqsExtended.receiveMessage(receiveMessageRequest).getMessages();

      // Print information about the message.
      for (Message message : messages) {
        logger.log("\nMessage received.");
        logger.log("  ID: " + message.getMessageId());
        logger.log("  Receipt handle: " + message.getReceiptHandle());
        logger.log("  Message body (first 5 characters): " + message.getBody().substring(0, 5));
      }

      // Delete the message
      final String messageReceiptHandle = messages.get(0).getReceiptHandle();
      sqsExtended.deleteMessage(new DeleteMessageRequest(myQueueUrl, messageReceiptHandle));
      logger.log("\nDeleted the message.");

    }
    catch (Exception exception)
    {
      logger.log(exception.toString());
    }

  }
}