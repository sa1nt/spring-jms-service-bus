# Pet project to play with Azure Service Bus from Java

The goal is to investigate how to use Azure Service Bus from a Spring Java program with minimal dependencies.

## TODO

1. investigate how to route messages from to different Subscriptions on a Topic, like in [RabbitMQ Topic exchange](https://www.rabbitmq.com/tutorials/tutorial-five-java.html)
2. investigate ways to allow access to a Topic from other Azure Subscriptions
   1. how to allow a consumer to create a Subscription for himself  
      I don't want to keep all the Topic Subscriptions in my IaC code  
      I also don't want to persuade consumers to change their SAS (or whatever) tokens when we decide to re-run our Terraform scripts that would recreate ASB resource 
3. see if setting up `@JmsListener`s without using Microsoft / Azure libraries and relying only on Spring stuff  
  Currently `@JmsListener` fails because the Spring auto-configured `jmsListenerContainerFactory` bean doesn't work with Standard ASB.  
  When using Azure `azure-spring-boot-starter-servicebus-jms` library, there appears a `topicJmsListenerContainerFactory` bean auto-configured in `com.azure.spring.autoconfigure.jms.AbstractServiceBusJMSAutoConfiguration` and it should be used when annotating with `@JmsListener`.
   * See https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/spring/azure-spring-boot/src/main/java/com/azure/spring/autoconfigure/jms
4. fix issue when `MappingJackson2MessageConverter.getJavaTypeForMessage` fails and messages cannot be deserialized for the `@JmsListener`  
   investigate if `MappingJackson2MessageConverter.setTypeIdMappings` would do the trick 

## Usage

### Setup 
1. Deploy `azure-template.json` e.g. via [Portal](https://portal.azure.com/#create/Microsoft.Template)
2. Copy some Shared Access Policy from Portal into the `az.servicebus.connection-string` in `applicaton.yml`
3. Run or Debug `Application.java`

### Sending messages 

To send a message do 
`> curl -X POST localhost:8080/queue?userName=Valera`

### Inspecting messages

It's easiest to inspect the state of the ASB Namespace using the [ServiceBusExplorer](https://github.com/paolosalvatori/ServiceBusExplorer) tool.
