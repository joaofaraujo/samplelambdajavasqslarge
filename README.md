# Lambda Java Producer SQS  
- Exemplo de Lambda que produz uma mensagem no SQS acima de 256KB
- O exemplo utiliza a seguinte LIB: https://github.com/awslabs/amazon-sqs-java-extended-client-lib 
  - Utilizado a versão 1, dado alguns problemas de compatibilidade entre o Extended e o SQSClient
  - https://github.com/aws/aws-sdk-java-v2/blob/master/docs/LaunchChangelog.md#7-high-level-libraries
- Mais detalhes sobre a funcionalidade: https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-s3-messages.html
