# Pet project to play with Azure Service Bus from Java

The goal is to investigate how to use Azure Service Bus from a Spring Java program with minimal dependencies.

## TODO

1. investigate how to route messages from to different Subscriptions on a Topic, like in [RabbitMQ Topic exchange](https://www.rabbitmq.com/tutorials/tutorial-five-java.html)
2. investigate ways to allow access to a Topic from other Azure Subscriptions
   1. how to allow a consumer to create a Subscription for himself  
      I don't want to keep all the Topic Subscriptions in my IaC code

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
